package com.mycompany.bookme.twilio;

import java.util.UUID;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.bookme.twilio.config.TwilioProperties;
import com.mycompany.bookme.twilio.utils.TwiMLHelper;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Say;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
    

@RestController
@RequestMapping("/api/v1/voice/bookings/process-speech")
@Slf4j
@RequiredArgsConstructor
public class SpeechProcessController {

    private final TwilioProperties twilioProperties;
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public String processSpeechResult(
            @RequestParam(value = "SpeechResult", required = false) String speechResult,
            @RequestParam(value = "Confidence", required = false) String confidenceStr,
            @RequestParam(value = "CallSid", required = false) String callSid) {
        log.info("Procesando resultado de la llamada: SpeechResult='{}', Confidence='{}', CallSid='{}'", speechResult, confidenceStr, callSid);

        final Double confidence = parse(confidenceStr);

        String conversationId = (callSid == null || callSid.isBlank())
                ? UUID.randomUUID().toString()
                : callSid;

        // guarda la entrada del usuario en la memoria
        if (speechResult != null && !speechResult.isBlank()) {
            chatMemory.add(conversationId, UserMessage.builder().text(speechResult).build());
        }

//        if (isNull(speechResult) || speechResult.trim().isEmpty() || isNull(confidence)) {
//            log.warn("SpeechResult con id: {} es nulo o vacío. Activando fallback.", callSid);
//            return getFallbackResponse();
//        }
        // Lógica basada en confianza
        final String replyText = chatClient.prompt(""" 
                        Eres una camarera de un restaurante encargada de agendar reservas en España, así que tienes que parecer Española, utilizando las tools que tienes a tu disposición.
                        Responde en español de manera clara y concisa.
                        Proporciona solo la respuesta que se debe dar al cliente, sin explicaciones adicionales.
                        No preguntes sobre restricciones dietéticas ni alergias.
                        Si el cliente menciona una hora o fecha no disponible, ofrece la siguiente opción disponible.
                        Utiliza un tono amable y profesional.
                        Sé lo más breve posible en tus respuestas y no te vayas por las ramas.
                        No agobies con demasiadas preguntas, una o dos preguntas máximo por respuesta.
                        """)
                .user(speechResult)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .messages()
                .tools(new DateTimeTools())
                .call()
                .content();

        chatMemory.add(conversationId, SystemMessage.builder().text(replyText).build());

//        if (confidence >= twilioProperties.getConfidenceThreshold()) {
//            // Confianza alta: usar SpeechResult
//            replyText = "Entiendo que dijo: " + speechResult.trim() + ". Le transferimos a un agente.";
//            log.info("SpeechResult aceptado con confianza {}: {}", confidence, speechResult);
//        } else if (confidence >= 0.4) {
//            // Confianza media: pedir confirmación
//            replyText = "Creo que dijo: " + speechResult.trim() + ". ¿Es correcto? Responda sí o no.";
//            log.info("Solicitando confirmación para SpeechResult '{}' con confianza {}", speechResult, confidence);
//        } else {
//            // Confianza baja o sin datos: fallback
//            replyText = "No pude identificar su respuesta claramente. Por favor, repita o marque 0 para hablar con un agente.";
//            log.warn("Fallback activado: SpeechResult='{}', Confidence={}", speechResult, confidence);
//        }

        final Say respuesta = TwiMLHelper.say(replyText);
        return new VoiceResponse.Builder()
                .say(respuesta)
                .build()
                .toXml();
    }

    private static String getFallbackResponse() {
        final Say respuestaFallback = TwiMLHelper.say("En que podemos ayudarle? No he podido escuchar su respuesta claramente.");
        return new VoiceResponse.Builder()
                .say(respuestaFallback)
                .build()
                .toXml();
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
