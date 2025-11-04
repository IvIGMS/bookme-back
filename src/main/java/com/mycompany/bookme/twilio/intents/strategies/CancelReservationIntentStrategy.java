package com.mycompany.bookme.twilio.intents.strategies;

import com.mycompany.bookme.twilio.dto.ConversationCtx;
import com.mycompany.bookme.twilio.dto.IntentType;
import com.mycompany.bookme.twilio.intents.IntentStrategy;

public class CancelReservationIntentStrategy implements IntentStrategy {
    @Override
    public IntentType getSupportedIntent() {
        return IntentType.CANCEL_RESERVATION;
    }

    @Override
    public String getReplyText(ConversationCtx conversationCtx) {
        // TODO: Implementar la lógica para cancelar la reserva y generar la respuesta adecuada en formato de texto.
        return "Hemos cancelado su reserva. ¿Hay algo más en lo que pueda ayudarle?";
    }
}
