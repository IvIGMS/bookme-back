package com.mycompany.bookme.twilio.intents.strategies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.bookme.tools.DateTimeTools;
import com.mycompany.bookme.twilio.dto.ConversationCtx;
import com.mycompany.bookme.twilio.intents.IntentType;
import com.mycompany.bookme.twilio.intents.IntentTypeStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class ProvideDateIntentTypeStrategy implements IntentTypeStrategy {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    @Override
    public IntentType getSupportedIntent() {
        return IntentType.PROVIDE_DATE;
    }

    @Override
    public String getReplyText(ConversationCtx ctx) {
        String json = chatClient.prompt("""
                        Eres un asistente de reservas de restaurantes. Ayudas a los usuarios a proporcionar la fecha de reserva.
                        Extrae la fecha de la reserva (reservationDate) del mensaje del usuario (formato: yyyy-MM-dd).
                        Responde siempre en formato JSON con la clave: reservationDate
                        Proporciona la respuesta en el siguiente formato:
                        {"reservationDate": "yyyy-MM-dd"}
                        Responde solo con el JSON solicitado, sin explicaciones adicionales.
                        Si no puedes extraer la fecha, asigna null a la clave, sin comillas, por ejemplo: "reservationDate": null.
                        Ten en cuenta que la fecha puede estar en formatos como "mañana", "el próximo viernes", etc., utiliza la tool para calcular fechas relativas.
                        Importante: No te inventes la fecha, solo extrae o calcula basándote en la tool si es relativo.
                        """)
                .user(ctx.getCurrentUserMessage())
                .tools(new DateTimeTools())
                .call()
                .content();

        try {
            // Crear un DTO simple para extraer solo la fecha
            DateExtractionDto extracted = objectMapper.readValue(json, DateExtractionDto.class);
            if (nonNull(extracted.reservationDate())) {
                ctx.setReservationDate(extracted.reservationDate());
                // Verificar qué datos faltan y preguntar por el siguiente
                if (isNull(ctx.getReservationHour())) {
                    return "¿A qué hora le gustaría reservar la mesa?";
                } else if (isNull(ctx.getPartySize())) {
                    return "¿Para cuántas personas sería la reserva?";
                } else {
                    return String.format(
                            "Entonces, desea reservar una mesa para el %s a las %s, para %d personas. ¿Es correcto?",
                            ctx.getReservationDate(),
                            ctx.getReservationHour(),
                            ctx.getPartySize());
                }
            } else {
                return "No pude entender la fecha proporcionada. ¿Podría repetirla en formato día/mes/año o describirla de otra manera?";
            }
        } catch (JsonProcessingException e) {
            return "Lo siento, hubo un error al procesar su solicitud. ¿Podría intentarlo de nuevo?";
        }
    }

    private record DateExtractionDto(LocalDate reservationDate) {}
}
