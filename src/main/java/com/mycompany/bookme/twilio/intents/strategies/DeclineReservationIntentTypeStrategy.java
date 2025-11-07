package com.mycompany.bookme.twilio.intents.strategies;

import com.mycompany.bookme.twilio.dto.ConversationCtx;
import com.mycompany.bookme.twilio.intents.IntentType;
import com.mycompany.bookme.twilio.intents.IntentTypeStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeclineReservationIntentTypeStrategy implements IntentTypeStrategy {

    @Override
    public IntentType getSupportedIntent() {
        return IntentType.DECLINE_RESERVATION;
    }

    @Override
    public String getReplyText(ConversationCtx ctx) {
        // TODO: Borrar cualquier dato de reserva de la BBDD si procede
        ctx.clearReservationDetails();
        return "Entiendo, no hay problema. Si cambia de opinión, estamos aquí para ayudarle. ¡Que tenga un buen día!";
    }
}