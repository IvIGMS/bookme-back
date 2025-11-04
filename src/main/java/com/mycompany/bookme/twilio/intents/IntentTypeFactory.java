package com.mycompany.bookme.twilio.intents;

import com.mycompany.bookme.twilio.dto.IntentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class IntentTypeFactory {

    private final Map<IntentType, IntentStrategy> strategies = new EnumMap<>(IntentType.class);

    public IntentTypeFactory(List<IntentStrategy> strategies) {
        for (IntentStrategy strategy : strategies) {
            this.strategies.put(strategy.getSupportedIntent(), strategy);
        }
    }

    public IntentStrategy getReplyTextIntentStrategy(IntentType intentType) {
        IntentStrategy intentStrategy = strategies.getOrDefault(intentType, strategies.get(IntentType.UNKNOWN));
        log.info("Using ReplyTextIntent strategy: {} for intent type: {}", intentStrategy.getClass().getSimpleName(), intentType);
        return intentStrategy;
    }

}
