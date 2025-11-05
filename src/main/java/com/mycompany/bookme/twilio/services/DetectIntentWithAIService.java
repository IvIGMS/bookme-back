package com.mycompany.bookme.twilio.services;

import com.mycompany.bookme.twilio.intents.IntentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DetectIntentWithAIService {
    private static final String PROMPT_TEMPLATE = """
            Eres un clasificador. Dada la frase de un cliente en un restaurante español, responde **solo** con UNA de las etiquetas EXACTAS listadas más abajo.
            Lista de etiquetas: %s
            Instrucciones:
             - Responde exactamente con una de las etiquetas, sin explicaciones, sin comillas, sin texto adicional.
             - Si tienes dudas, responde UNKNOWN.
            """;

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public IntentType detect(String userInput, String conversationId) {
        log.info("Detecting intent for user input: {}", userInput);
        List<Message> memoryMessages = chatMemory.get(conversationId);
        String classification = getLlmClassification(memoryMessages, userInput);
        IntentType intentType = IntentType.fromStringOrDefault(classification);
        log.info("Detected intent type: {} (raw LLM: '{}')", intentType, classification);
        return intentType;
    }

    private String getIntentTypesDelimitedByCommas() {
        return IntentType.valuesList()
                .stream()
                .map(IntentType::name)
                .collect(Collectors.joining(", "));
    }

    private String getLlmClassification(List<Message> memoryMessages, String userInput) {
        String intentTypes = getIntentTypesDelimitedByCommas();
        String systemInstruction = String.format(PROMPT_TEMPLATE, intentTypes);

        // Construimos prompt que incluye memoria (si existe) y el texto actual.
        Prompt prompt = new Prompt(memoryMessages == null ? List.of() : memoryMessages);

        String userInstruction = "Frase a clasificar: \"" + userInput.trim() + "\"";

        String llmIntentClassification = chatClient
                .prompt(prompt)
                .system(systemInstruction)
                .user(userInstruction)
                .call()
                .content();

        if (llmIntentClassification == null || llmIntentClassification.trim().isEmpty()) {
            log.warn("LLM returned null or empty classification, defaulting to UNKNOWN");
            return IntentType.UNKNOWN.name();
        }

        log.info("LLM classified user input as intent type '{}'", llmIntentClassification);
        return llmIntentClassification.trim();
    }


}
