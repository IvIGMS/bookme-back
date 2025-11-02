package com.mycompany.bookme.twilio;

import com.mycompany.bookme.twilio.config.TwilioProperties;
import com.mycompany.bookme.twilio.utils.TwiMLHelper;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Say;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.isNull;

@RestController
@RequestMapping("/api/v1/voice/bookings/process-speech")
@Slf4j
@RequiredArgsConstructor
public class SpeechProcessController {

    private final TwilioProperties twilioProperties;
    private final ChatClient chatClient;

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public String processSpeechResult(
            @RequestParam(value = "SpeechResult", required = false) String speechResult,
            @RequestParam(value = "Confidence", required = false) String confidenceStr,
            @RequestParam(value = "CallSid", required = false) String callSid) {
        log.info("Procesando resultado de la llamada: SpeechResult='{}', Confidence='{}', CallSid='{}'", speechResult, confidenceStr, callSid);

        final Double confidence = parse(confidenceStr);

        if (isNull(speechResult) || speechResult.trim().isEmpty() || isNull(confidence)) {
            log.warn("SpeechResult con id: {} es nulo o vacío. Activando fallback.", callSid);
            return getFallbackResponse();
        }

        // Lógica basada en confianza
        final String replyText;
        if (confidence >= twilioProperties.getConfidenceThreshold()) {
            // Confianza alta: usar SpeechResult
            replyText = "Entiendo que dijo: " + speechResult.trim() + ". Le transferimos a un agente.";
            log.info("SpeechResult aceptado con confianza {}: {}", confidence, speechResult);
        } else if (confidence >= 0.4) {
            // Confianza media: pedir confirmación
            replyText = "Creo que dijo: " + speechResult.trim() + ". ¿Es correcto? Responda sí o no.";
            log.info("Solicitando confirmación para SpeechResult '{}' con confianza {}", speechResult, confidence);
        } else {
            // Confianza baja o sin datos: fallback
            replyText = "No pude identificar su respuesta claramente. Por favor, repita o marque 0 para hablar con un agente.";
            log.warn("Fallback activado: SpeechResult='{}', Confidence={}", speechResult, confidence);
        }

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
