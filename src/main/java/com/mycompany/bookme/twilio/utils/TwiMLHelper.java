package com.mycompany.bookme.twilio.utils;

import com.twilio.twiml.voice.Say;

public class TwiMLHelper {

    public static Say say(String message) {
        return new Say.Builder(message)
                .language(Say.Language.ES_ES)
                .voice(Say.Voice.ALICE)
                .build();
    }
}
