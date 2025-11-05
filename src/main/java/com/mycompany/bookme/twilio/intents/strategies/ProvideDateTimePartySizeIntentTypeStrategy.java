package com.mycompany.bookme.twilio.intents.strategies;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.bookme.twilio.dto.ConversationCtx;
import com.mycompany.bookme.twilio.intents.IntentTypeStrategy;
import com.mycompany.bookme.twilio.intents.IntentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Slf4j
@Component
public class ProvideDateTimePartySizeIntentTypeStrategy implements IntentTypeStrategy {

    private final ObjectMapper objectMapper;
    private final ChatClient chatClient;

    @Override
    public IntentType getSupportedIntent() {
        return IntentType.PROVIDE_DATE_TIME_PARTY_SIZE;
    }

    @Override
    public String getReplyText(ConversationCtx ctx) {
        String json = chatClient.prompt(getPrompt())
                .user(ctx.getCurrentUserMessage())
                .call()
                .content();
        try {
            ConversationCtx extractedInfo = objectMapper.convertValue(json, ConversationCtx.class);
            if (nonNull(extractedInfo.getReservationDate())) {
                ctx.setReservationDate(extractedInfo.getReservationDate());
            }
            if (nonNull(extractedInfo.getReservationHour())) {
                ctx.setReservationHour(extractedInfo.getReservationHour());
            }
            if (nonNull(extractedInfo.getPartySize())) {
                ctx.setPartySize(extractedInfo.getPartySize());
            }
        } catch (Exception e) {
            // Log the exception (omitted for brevity)
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

    private static String getPrompt() {
        return """
                Eres un asistente de reservas de restaurantes. Ayudas a los usuarios a reservar mesas.
                Extrae la siguiente información de la solicitud del usuario:
                1. Fecha de la reserva (reservationDate) (formato: DD/MM/AAAA)
                2. Hora de la reserva (reservationHour) (formato: HH:MM)
                3. Número de personas (partySize) (entero)
                Responde siempre en formato JSON con las claves: reservationDate, reservationHour, partySize
                Si no puedes extraer alguno de los datos, asigna null a esa clave.
                Proporciona la respuesta en el siguiente formato:
                {"reservationDate": "DD/MM/AAAA", "reservationHour": "HH:MM", "partySize": N}
                Responde solo con el JSON solicitado, sin explicaciones adicionales.
                """;
    }
}
