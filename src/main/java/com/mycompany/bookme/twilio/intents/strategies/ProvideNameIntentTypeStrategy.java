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

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class ProvideNameIntentTypeStrategy implements IntentTypeStrategy {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    @Override
    public IntentType getSupportedIntent() {
        return IntentType.PROVIDE_NAME;
    }

    @Override
    public String getReplyText(ConversationCtx ctx) {
        String json = chatClient.prompt("""
                        Eres un asistente de reservas de restaurantes. Ayudas a los usuarios a proporcionar el nombre para la reserva.
                        Extrae el nombre de la reserva (reservationName) del mensaje del usuario (cadena de texto).
                        Responde siempre en formato JSON con la clave: reservationName
                        Proporciona la respuesta en el siguiente formato:
                        {"reservationName": "Nombre del usuario"}
                        Responde solo con el JSON solicitado, sin explicaciones adicionales.
                        Si no puedes extraer el nombre, asigna null a la clave, sin comillas, por ejemplo: "reservationName": null.
                        Importante: Solo extrae el nombre mencionado por el usuario.
                        """)
                .user(ctx.getCurrentUserMessage())
                .call()
                .content();

        try {
            // Crear un DTO simple para extraer solo el nombre
            NameExtractionDto extracted = objectMapper.readValue(json, NameExtractionDto.class);
            if (nonNull(extracted.reservationName())) {
                ctx.setReservationName(extracted.reservationName());
                return ConversationUtils.getNextQuestionOrConfirmation(ctx);
            } else {
                return "No pude entender el nombre proporcionado. ¿Podría repetir el nombre para la reserva?";
            }
        } catch (JsonProcessingException e) {
            return "Lo siento, hubo un error al procesar su solicitud. ¿Podría intentarlo de nuevo?";
        }
    }

    private record NameExtractionDto(String reservationName) {}
}