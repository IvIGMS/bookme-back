package com.mycompany.bookme.twilio.intents.strategies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.bookme.tools.DateTimeTools;
import com.mycompany.bookme.twilio.dto.ConversationCtx;
import com.mycompany.bookme.twilio.intents.IntentType;
import com.mycompany.bookme.twilio.intents.IntentTypeStrategy;
import com.mycompany.bookme.twilio.utils.ConversationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class ProvideDateAndTimeIntentTypeStrategy implements IntentTypeStrategy {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    @Override
    public IntentType getSupportedIntent() {
        return IntentType.PROVIDE_DATE_AND_TIME;
    }

    @Override
    public String getReplyText(ConversationCtx ctx) {
        String json = chatClient.prompt("""
                        Eres un asistente de reservas de restaurantes. Ayudas a los usuarios a proporcionar la fecha y hora de reserva.
                        Extrae la siguiente información del mensaje del usuario:
                        1. Fecha de la reserva (reservationDate) (formato: yyyy-MM-dd)
                        2. Hora de la reserva (reservationHour) (formato: HH:mm)
                        
                        Responde SIEMPRE en formato JSON con las claves: reservationDate, reservationHour.
                        Ejemplo de salida: {"reservationDate": "2025-05-21", "reservationHour": "20:00"}
                        
                        Si no puedes extraer alguno de los datos, usa null (sin comillas).
                        Ejemplo: {"reservationDate": null, "reservationHour": "20:00"}
                        
                        Ten en cuenta que la fecha puede estar en formatos como "mañana", "el próximo viernes", etc., utiliza la tool para calcular fechas relativas.
                        Importante: No te inventes la fecha y hora, solo extrae o calcula basándote en la tool si es relativo.
                        """)
                .user(ctx.getCurrentUserMessage())
                .tools(new DateTimeTools())
                .call()
                .content();

        try {
            DateTimeExtractionDto extracted = objectMapper.readValue(json, DateTimeExtractionDto.class);
            if (nonNull(extracted.reservationDate())) {
                ctx.setReservationDate(extracted.reservationDate());
            }
            if (nonNull(extracted.reservationHour())) {
                ctx.setReservationHour(extracted.reservationHour());
            }
            return ConversationUtils.getNextQuestionOrConfirmation(ctx);
        } catch (JsonProcessingException e) {
            return "Lo siento, hubo un error al procesar su solicitud. ¿Podría intentarlo de nuevo?";
        }
    }

    private record DateTimeExtractionDto(java.time.LocalDate reservationDate, java.time.LocalTime reservationHour) {}
}