package com.mycompany.bookme.twilio.intents;

import com.mycompany.bookme.twilio.dto.ConversationCtx;
import com.mycompany.bookme.twilio.dto.IntentType;

public interface IntentStrategy {
    IntentType getSupportedIntent();
    String getReplyText(ConversationCtx ctx);
}
