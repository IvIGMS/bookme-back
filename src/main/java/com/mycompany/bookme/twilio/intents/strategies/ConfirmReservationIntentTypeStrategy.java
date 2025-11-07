package com.mycompany.bookme.twilio.intents.strategies;

import com.mycompany.bookme.twilio.dto.ConversationCtx;
import com.mycompany.bookme.twilio.intents.IntentType;
import com.mycompany.bookme.twilio.intents.IntentTypeStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfirmReservationIntentTypeStrategy implements IntentTypeStrategy {

    @Override
    public IntentType getSupportedIntent() {
        return IntentType.CONFIRM_RESERVATION;
    }

    @Override
    public String getReplyText(ConversationCtx ctx) {
        // Verificar datos faltantes uno por uno y preguntar específicamente
        if (ctx.getReservationName() == null) {
            return "Por favor, ¿a nombre de quién hago la reserva?";
        }
        if (ctx.getReservationDate() == null) {
            return "Por favor, ¿para qué fecha quieres la reserva?";
        }
        if (ctx.getReservationHour() == null) {
            return "Por favor, ¿a qué hora prefieres la reserva?";
        }
        if (ctx.getPartySize() == null) {
            return "Por favor, ¿para cuántas personas es la reserva?";
        }

        // Si todos los datos están presentes, confirmar la reserva
        // TODO: Guardar la reserva en la base de datos
        return String.format(
                "¡Perfecto! Su reserva ha sido confirmada para el %s a las %s, para %d personas a nombre de %s. ¡Gracias por elegirnos!",
                ctx.getReservationDate(),
                ctx.getReservationHour(),
                ctx.getPartySize(),
                ctx.getReservationName());
    }
}