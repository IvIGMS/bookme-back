package com.mycompany.bookme.twilio.intents.strategies;

import com.mycompany.bookme.twilio.intents.IntentType;
import com.mycompany.bookme.twilio.intents.ReplyTextIntent;

public class CancelReservationIntentStrategy implements ReplyTextIntent {
    @Override
    public IntentType getSupportedIntent() {
        return IntentType.CANCEL_RESERVATION;
    }

    @Override
    public String getReplyText() {
        // TODO: Implementar la lógica para cancelar la reserva y generar la respuesta adecuada en formato de texto.
        return "Hemos cancelado su reserva. ¿Hay algo más en lo que pueda ayudarle?";
    }
}
