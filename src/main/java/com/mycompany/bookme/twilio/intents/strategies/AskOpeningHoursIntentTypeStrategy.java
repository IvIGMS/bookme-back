package com.mycompany.bookme.twilio.intents.strategies;

import com.mycompany.bookme.twilio.dto.ConversationCtx;
import com.mycompany.bookme.twilio.intents.IntentType;
import com.mycompany.bookme.twilio.intents.IntentTypeStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AskOpeningHoursIntentTypeStrategy implements IntentTypeStrategy {

    @Override
    public IntentType getSupportedIntent() {
        return IntentType.ASK_OPENING_HOURS;
    }

    @Override
    public String getReplyText(ConversationCtx ctx) {
        //TODO: Crear una tool que obtenga el horario de apertura real desde la base de datos o configuración
        return "Nuestro horario de apertura es de lunes a domingo de 12:00 a 23:00. ¿Le gustaría hacer una reserva?";
    }
}