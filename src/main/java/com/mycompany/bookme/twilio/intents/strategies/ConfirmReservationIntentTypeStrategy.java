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
        if (ctx.getReservationDate() != null && ctx.getReservationHour() != null && ctx.getPartySize() != null && ctx.getReservationName() != null) {
            // TODO: Guardar la reserva en la base de datos
            return String.format(
                    "¡Perfecto! Su reserva ha sido confirmada para el %s a las %s, para %d personas a nombre de %s. ¡Gracias por elegirnos!",
                    ctx.getReservationDate(),
                    ctx.getReservationHour(),
                    ctx.getPartySize(),
                    ctx.getReservationName());
        } else {
            return "No tengo suficiente información para confirmar la reserva. Por favor, proporcione todos los detalles necesarios.";
        }
    }
}