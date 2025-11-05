package com.mycompany.bookme.twilio.intents.strategies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.bookme.twilio.dto.ConversationCtx;
import com.mycompany.bookme.twilio.intents.IntentType;
import com.mycompany.bookme.twilio.intents.IntentTypeStrategy;
import com.mycompany.bookme.twilio.utils.ConversationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class ProvideTimeIntentTypeStrategy implements IntentTypeStrategy {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    @Override
    public IntentType getSupportedIntent() {
        return IntentType.PROVIDE_TIME;
    }

    @Override
    public String getReplyText(ConversationCtx ctx) {
        String json = chatClient.prompt("""
                        Eres un asistente de reservas de restaurantes. Ayudas a los usuarios a proporcionar la hora de reserva.
                        Extrae la hora de la reserva (reservationHour) del mensaje del usuario (formato: HH:mm).
                        Responde siempre en formato JSON con la clave: reservationHour
                        Proporciona la respuesta en el siguiente formato:
                        {"reservationHour": "HH:mm"}
                        Responde solo con el JSON solicitado, sin explicaciones adicionales.
                        Si no puedes extraer la hora, asigna null a la clave, sin comillas, por ejemplo: "reservationHour": null.
                        Ten en cuenta que la hora puede estar en formatos de 12 horas con AM/PM o 24 horas.
                        Importante: No te inventes la hora, solo extrae lo que el usuario ha mencionado.
                        """)
                .user(ctx.getCurrentUserMessage())
                .call()
                .content();

        try {
            // Crear un DTO simple para extraer solo la hora
            TimeExtractionDto extracted = objectMapper.readValue(json, TimeExtractionDto.class);
            if (nonNull(extracted.reservationHour())) {
                ctx.setReservationHour(extracted.reservationHour());
                return ConversationUtils.getNextQuestionOrConfirmation(ctx);
            } else {
                return "No pude entender la hora proporcionada. ¿Podría repetirla en formato hora:minutos o describirla de otra manera?";
            }
        } catch (JsonProcessingException e) {
            return "Lo siento, hubo un error al procesar su solicitud. ¿Podría intentarlo de nuevo?";
        }
    }

    private record TimeExtractionDto(LocalTime reservationHour) {}
}
