package com.mycompany.bookme.twilio.intents.strategies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.bookme.tools.DateTimeTools;
import com.mycompany.bookme.twilio.dto.ConversationCtx;
import com.mycompany.bookme.twilio.intents.IntentTypeStrategy;
import com.mycompany.bookme.twilio.intents.IntentType;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class BookTableIntentTypeStrategy implements IntentTypeStrategy {

    public static final String LOCALE_ES = "es_ES";
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    @Override
    public IntentType getSupportedIntent() {
        return IntentType.BOOK_TABLE;
    }

    @Override
    public String getReplyText(ConversationCtx ctx) {

        String systemInstructions = getSystemInstructions();
        String json = chatClient.prompt()
                .system(systemInstructions)
                .user(ctx.getCurrentUserMessage())
                .tools(new DateTimeTools())
                .call()
                .content();
        try {
            ConversationCtx extractedInfo = objectMapper.readValue(json, ConversationCtx.class);
            if (nonNull(extractedInfo.getReservationDate())) {
                ctx.setReservationDate(extractedInfo.getReservationDate());
            }
            if (nonNull(extractedInfo.getReservationHour())) {
                ctx.setReservationHour(extractedInfo.getReservationHour());
            }
            if (nonNull(extractedInfo.getPartySize())) {
                ctx.setPartySize(extractedInfo.getPartySize());
            }

        } catch (IllegalArgumentException e) {
            return "Lo siento, no pude entender su solicitud. ¿Podría reformularla?";
        } catch (JsonProcessingException e) {
            return "Lo siento, hubo un error al procesar su solicitud. ¿Podría intentarlo de nuevo?";
        }

        if (isNull(ctx.getReservationDate())) {
            return "¿Para qué fecha desea la reserva?";
        }
        if (isNull(ctx.getReservationHour())) {
            return "¿A qué hora le gustaría reservar la mesa?";
        }
        if (isNull(ctx.getPartySize())) {
            return "¿Para cuántas personas sería la reserva?";
        }

        // Todos los datos presentes
        return String.format(
                "Entonces, desea reservar una mesa para el %s a las %s, para %d personas. ¿Es correcto?",
                ctx.getReservationDate(),
                ctx.getReservationHour(),
                ctx.getPartySize()
        );
    }

    private static String getSystemInstructions() {
        return """
                Eres un asistente de reservas de restaurantes. Ayudas a los usuarios a reservar mesas.
                Extrae la siguiente información de la solicitud del usuario:
                1. Fecha de la reserva (reservationDate) (formato: yyyy-MM-dd)
                2. Hora de la reserva (reservationHour) (formato:HH:mm)
                3. Número de personas (partySize) (entero)
                Responde siempre en formato JSON con las claves: reservationDate, reservationHour, partySize
                Proporciona la respuesta en el siguiente formato:
                {"reservationDate": "yyyy-MM-dd", "reservationHour": "HH:mm", "partySize": N}
                Responde solo con el JSON solicitado, sin explicaciones adicionales.
                Si no puedes extraer alguno de los datos, asigna null a esa clave, sin comillas, por ejemplo: "partySize": null.
                Ten en cuenta que la hora puede estar en formato de 12 horas con AM/PM o en formato de 24 horas.
                Importante: Ten en cuenta el contexto de la conversación previa para interpretar correctamente la solicitud del usuario.
                Importante: No te inventes la fecha y la hora, solo extrae lo que el usuario ha mencionado en su mensaje, solo utiliza la fecha proporcionada por la tool para calcular fechas relativas como "el próximo viernes" o "dentro de dos semanas".
                """;
    }
}
