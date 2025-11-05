package com.mycompany.bookme.twilio.intents.strategies;

import com.mycompany.bookme.twilio.dto.ConversationCtx;
import com.mycompany.bookme.twilio.intents.IntentType;
import com.mycompany.bookme.twilio.intents.IntentTypeStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModifyReservationIntentTypeStrategy implements IntentTypeStrategy {

    @Override
    public IntentType getSupportedIntent() {
        return IntentType.MODIFY_RESERVATION;
    }

    @Override
    public String getReplyText(ConversationCtx ctx) {
        // TODO: Implementar la lógica para modificar la reserva existente
        return "Entiendo que desea modificar su reserva. ¿Qué le gustaría cambiar?";
    }
}