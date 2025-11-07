package com.mycompany.bookme.twilio.intents;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class IntentTypeFactory {

    private final Map<IntentType, IntentTypeStrategy> strategies = new EnumMap<>(IntentType.class);

    public IntentTypeFactory(List<IntentTypeStrategy> strategies) {
        if(strategies.isEmpty()) {
            log.warn("No IntentStrategy implementations found!");
        }
        for (IntentTypeStrategy strategy : strategies) {
            this.strategies.put(strategy.getSupportedIntent(), strategy);
        }
    }

    public IntentTypeStrategy getReplyTextIntentStrategy(IntentType intentType) {
        IntentTypeStrategy intentTypeStrategy = strategies.getOrDefault(intentType, strategies.get(IntentType.UNKNOWN));
        log.info("Using ReplyTextIntent strategy: {} for intent type: {}", intentTypeStrategy.getClass().getSimpleName(), intentType);
        return intentTypeStrategy;
    }

}
