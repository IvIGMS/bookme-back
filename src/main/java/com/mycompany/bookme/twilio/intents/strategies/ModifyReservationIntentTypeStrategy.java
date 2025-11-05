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

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class ModifyReservationIntentTypeStrategy implements IntentTypeStrategy {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    @Override
    public IntentType getSupportedIntent() {
        return IntentType.MODIFY_RESERVATION;
    }

    @Override
    public String getReplyText(ConversationCtx ctx) {
        String json = chatClient.prompt("""
                        Eres un asistente de reservas de restaurantes. El usuario quiere modificar su reserva existente.
                        Identifica qué campo quiere modificar y el nuevo valor basado en su mensaje.
                        Campos posibles: reservationDate (formato yyyy-MM-dd), reservationHour (formato HH:mm), partySize (entero), reservationName (cadena).
                        Responde SIEMPRE en formato JSON con las claves: field, newValue.
                        Ejemplo: {"field": "reservationDate", "newValue": "2025-11-06"}
                        Si no puedes identificar claramente, usa {"field": null, "newValue": null}
                        Para fechas relativas como "mañana", calcula usando la tool.
                        """)
                .user(ctx.getCurrentUserMessage())
                .tools(new DateTimeTools())
                .call()
                .content();

        try {
            ModificationDto extracted = objectMapper.readValue(json, ModificationDto.class);
            if (extracted.field() == null) {
                return "Entiendo que desea modificar su reserva. ¿Qué le gustaría cambiar: fecha, hora, número de personas o nombre?";
            }
            if (extracted.newValue() == null) {
                return "Entiendo que quiere cambiar " + extracted.field() + ". ¿Cuál es el nuevo valor?";
            }
            // Proceder con la modificación
            switch (extracted.field()) {
                case "reservationDate" -> ctx.setReservationDate(LocalDate.parse(extracted.newValue()));
                case "reservationHour" -> ctx.setReservationHour(LocalTime.parse(extracted.newValue()));
                case "partySize" -> ctx.setPartySize(Integer.valueOf(extracted.newValue()));
                case "reservationName" -> ctx.setReservationName(extracted.newValue());
                default -> {
                    return "No pude identificar qué campo modificar. Por favor, especifique claramente qué cambiar.";
                }
            }
            return "Modificación realizada. " + ConversationUtils.getNextQuestionOrConfirmation(ctx);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            return "Lo siento, hubo un error al procesar la modificación. ¿Podría intentarlo de nuevo?";
        }
    }

    private record ModificationDto(String field, String newValue) {}
}