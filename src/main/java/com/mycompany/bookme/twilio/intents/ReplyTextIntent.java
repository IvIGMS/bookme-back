package com.mycompany.bookme.twilio.intents;

import com.mycompany.bookme.twilio.dto.IntentType;

public interface ReplyTextIntent {
    IntentType getSupportedIntent();
    String getReplyText();
}
