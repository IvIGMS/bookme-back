package com.mycompany.bookme.twilio.intents;

import com.mycompany.bookme.twilio.dto.IntentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ReplayTextIntentFactory {

    private final Map<IntentType, ReplyTextIntent> strategies = new EnumMap<>(IntentType.class);

    public ReplayTextIntentFactory(List<ReplyTextIntent> strategies) {
        for (ReplyTextIntent strategy : strategies) {
            this.strategies.put(strategy.getSupportedIntent(), strategy);
        }
    }

    public ReplyTextIntent getReplyTextIntentStrategy(IntentType intentType) {
        ReplyTextIntent replyTextIntent = strategies.getOrDefault(intentType, strategies.get(IntentType.UNKNOWN));
        log.info("Using ReplyTextIntent strategy: {} for intent type: {}", replyTextIntent.getClass().getSimpleName(), intentType);
        return replyTextIntent;
    }

}
