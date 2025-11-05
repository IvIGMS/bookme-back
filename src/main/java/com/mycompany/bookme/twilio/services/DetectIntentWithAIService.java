package com.mycompany.bookme.twilio.services;

import com.mycompany.bookme.twilio.intents.IntentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DetectIntentWithAIService {
    private static final String PROMPT = """
            Dada la frase de un cliente en un restaurante español, clasifica su intención
            en una de las siguientes categorías exactas: %s.
            Responde solo con la etiqueta.
            """;
    private final ChatClient chatClient;

    public IntentType detect(String userInput) {
        log.info("Detecting intent for user input: {}", userInput);
        String intentTypes = getIntentTypesDelimitedByCommas();
        String classification = getLlmClassification(userInput, intentTypes);
        IntentType intentType = IntentType.fromStringOrDefault(classification);
        log.info("Detected intent type: {}", intentType);
        return intentType;
    }

    private String getLlmClassification(String userInput, String intentTypes) {
        String content = chatClient.prompt(PROMPT.formatted(intentTypes))
                .user(userInput)
                .call()
                .content();
        if(content == null || content.trim().isEmpty()) {
            log.warn("LLM returned null or empty classification, defaulting to UNKNOWN");
            return IntentType.UNKNOWN.name();
        }
        String llmIntentClassification = content.trim().toUpperCase();
        log.info("LLM classified user input as intent type '{}'", llmIntentClassification);
        return llmIntentClassification;
    }

    private static String getIntentTypesDelimitedByCommas() {
        return IntentType.valuesList()
                .stream()
                .map(IntentType::name)
                .collect(Collectors.joining(", "));
    }
}
