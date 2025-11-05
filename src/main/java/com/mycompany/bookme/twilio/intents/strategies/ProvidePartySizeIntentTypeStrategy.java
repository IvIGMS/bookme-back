package com.mycompany.bookme.twilio.intents.strategies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.bookme.twilio.dto.ConversationCtx;
import com.mycompany.bookme.twilio.intents.IntentType;
import com.mycompany.bookme.twilio.intents.IntentTypeStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class ProvidePartySizeIntentTypeStrategy implements IntentTypeStrategy {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    @Override
    public IntentType getSupportedIntent() {
        return IntentType.PROVIDE_PARTY_SIZE;
    }

    @Override
    public String getReplyText(ConversationCtx ctx) {
        String json = chatClient.prompt("""
                        Eres un asistente de reservas de restaurantes. Ayudas a los usuarios a proporcionar el número de personas para la reserva.
                        Extrae el número de personas (partySize) del mensaje del usuario (un entero).
                        Responde siempre en formato JSON con la clave: partySize
                        Proporciona la respuesta en el siguiente formato:
                        {"partySize": N}
                        Responde solo con el JSON solicitado, sin explicaciones adicionales.
                        Si no puedes extraer el número, asigna null a la clave, sin comillas, por ejemplo: "partySize": null.
                        Importante: Solo extrae números enteros válidos para el número de personas.
                        """)
                .user(ctx.getCurrentUserMessage())
                .call()
                .content();

        try {
            // Crear un DTO simple para extraer solo el número de personas
            PartySizeExtractionDto extracted = objectMapper.readValue(json, PartySizeExtractionDto.class);
            if (nonNull(extracted.partySize())) {
                ctx.setPartySize(extracted.partySize());
                // Verificar qué datos faltan y preguntar por el siguiente
                if (isNull(ctx.getReservationDate())) {
                    return "¿Para qué fecha desea la reserva?";
                } else if (isNull(ctx.getReservationHour())) {
                    return "¿A qué hora le gustaría reservar la mesa?";
                } else {
                    return String.format(
                            "Entonces, desea reservar una mesa para el %s a las %s, para %d personas. ¿Es correcto?",
                            ctx.getReservationDate(),
                            ctx.getReservationHour(),
                            ctx.getPartySize());
                }
            } else {
                return "No pude entender el número de personas proporcionado. ¿Podría repetir el número de comensales?";
            }
        } catch (JsonProcessingException e) {
            return "Lo siento, hubo un error al procesar su solicitud. ¿Podría intentarlo de nuevo?";
        }
    }

    private record PartySizeExtractionDto(Integer partySize) {}

}
