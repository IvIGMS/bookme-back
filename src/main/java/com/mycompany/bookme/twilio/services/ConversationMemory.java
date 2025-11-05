package com.mycompany.bookme.twilio.services;

import com.mycompany.bookme.twilio.dto.ConversationCtx;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConversationMemory {

    // Map<CallSid, ConversationCtx>
    private final Map<String, ConversationCtx> sessions = new ConcurrentHashMap<>();

    public ConversationCtx get(String phone) {
        return sessions.get(phone);
    }

    public ConversationCtx save(String conversationId, ConversationCtx ctx) {
        sessions.put(conversationId, ctx);
        return ctx;
    }

    public void clear(String phone) {
        sessions.remove(phone);
    }

    public ConversationCtx getOrCreate(String conversationId) {
        return sessions.computeIfAbsent(conversationId, k -> new ConversationCtx());
    }
}
