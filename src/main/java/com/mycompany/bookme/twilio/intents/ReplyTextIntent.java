package com.mycompany.bookme.twilio.intents;

public interface ReplyTextIntent {
    IntentType getSupportedIntent();
    String getReplyText();
}
