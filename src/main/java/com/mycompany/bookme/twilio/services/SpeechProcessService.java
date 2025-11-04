package com.mycompany.bookme.twilio.services;

import java.util.UUID;

import com.mycompany.bookme.twilio.dto.ConversationCtx;
import com.mycompany.bookme.twilio.dto.IntentType;
import com.mycompany.bookme.twilio.intents.IntentTypeFactory;
import com.mycompany.bookme.twilio.intents.IntentStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.mycompany.bookme.twilio.config.TwilioProperties;
import com.mycompany.bookme.twilio.utils.TwiMLHelper;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Say;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.isNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpeechProcessService {

    private final TwilioProperties twilioProperties;
    private final ChatMemory chatMemory;
    private final DetectIntentWithAIService detectIntentWithAIService;
    private final IntentTypeFactory replayTextIntentTypeFactory;

    public String processSpeechResult(
            @RequestParam(value = "SpeechResult", required = false) String speechResult,
            @RequestParam(value = "Confidence", required = false) String confidenceStr,
            @RequestParam(value = "CallSid", required = false) String callSid) {
        log.info("Procesando resultado de la llamada: SpeechResult='{}', Confidence='{}', CallSid='{}'", speechResult, confidenceStr, callSid);
        final Double confidence = parse(confidenceStr);
        if (isInvalid(speechResult) || isNull(confidence)) {
            log.warn("SpeechResult con id: {} es nulo o vacío. Activando fallback.", callSid);
            return getFallbackResponse();
        }
        String conversationId = getConversationId(callSid);
        saveUserChatMemory(conversationId, speechResult);
        IntentType intent = detectIntentWithAIService.detect(speechResult);
        String replyText = getReplyText(intent, new ConversationCtx());
        saveSystemChatMemory(conversationId, replyText);
        return buildTwilioResponse(replyText);
    }

    private static String buildTwilioResponse(String replyText) {
        final Say respuesta = TwiMLHelper.say(replyText);
        return new VoiceResponse.Builder()
                .say(respuesta)
                .build()
                .toXml();
    }

    private String getReplyText(IntentType intent, ConversationCtx ctx) {
        log.info("Generating reply text for intent: {}", intent);
        IntentStrategy strategy = replayTextIntentTypeFactory.getReplyTextIntentStrategy(intent);
        String replyText = strategy.getReplyText(ctx);
        log.info("Reply text generated for intent {}: {}", intent, replyText);
        return replyText;
    }

    private void saveSystemChatMemory(String conversationId, String replyText) {
        chatMemory.add(conversationId, SystemMessage.builder().text(replyText).build());
    }

    private void saveUserChatMemory(String conversationId, String text) {
        chatMemory.add(conversationId, UserMessage.builder().text(text).build());
    }

    private static String getConversationId(String callSid) {
        String conversationId = callSid == null || callSid.isBlank()
                ? UUID.randomUUID().toString()
                : callSid;
        log.debug("Using conversation ID: {}", conversationId);
        return conversationId;
    }

    private static boolean isInvalid(String speechResult) {
        return isNull(speechResult) || speechResult.trim().isEmpty() || speechResult.isBlank();
    }

    private static String getFallbackResponse() {
        return buildTwilioResponse("En que podemos ayudarle? No he podido escuchar su respuesta claramente.");
    }

    private static Double parse(String confidenceStr) {
        Double confidence = null;
        if (confidenceStr != null && !confidenceStr.trim().isEmpty()) {
            try {
                confidence = Double.valueOf(confidenceStr.trim());
                log.debug("Confidence parseada: {}", confidence);
            } catch (NumberFormatException e) {
                log.warn("Error parseando Confidence '{}': {}", confidenceStr, e.getMessage());
            }
        }
        return confidence;
    }
}
