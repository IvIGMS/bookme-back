package com.mycompany.bookme.twilio.intents;

import com.mycompany.bookme.twilio.dto.ConversationCtx;

public interface IntentTypeStrategy {
    IntentType getSupportedIntent();
    String getReplyText(ConversationCtx ctx);
}
