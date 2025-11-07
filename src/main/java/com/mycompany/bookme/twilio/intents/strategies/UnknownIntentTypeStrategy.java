package com.mycompany.bookme.twilio.intents.strategies;


import com.mycompany.bookme.twilio.dto.ConversationCtx;
import com.mycompany.bookme.twilio.intents.IntentType;
import com.mycompany.bookme.twilio.intents.IntentTypeStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnknownIntentTypeStrategy implements IntentTypeStrategy {
    @Override
    public IntentType getSupportedIntent() {
        return IntentType.UNKNOWN;
    }

    @Override
    public String getReplyText(ConversationCtx ctx) {
        return "Lo siento, no he entendido su solicitud. ¿Podría reformularla?";
    }
}
