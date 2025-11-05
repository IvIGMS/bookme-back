package com.mycompany.bookme.twilio.intents;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public enum IntentType {
    BOOK_TABLE,
    CANCEL_RESERVATION,
    MODIFY_RESERVATION,
    CONFIRM_RESERVATION,
    DECLINE_RESERVATION,
    ASK_OPENING_HOURS,
    PROVIDE_TIME,
    PROVIDE_DATE,
    PROVIDE_DATE_AND_TIME,
    PROVIDE_PARTY_SIZE,
    PROVIDE_DATE_AND_TIME_AND_PARTY_SIZE,
    UNKNOWN;

    private static final IntentType[] VALUES = values();
    private static final List<IntentType> VALUES_LIST = List.of(VALUES);
    private static final Map<String, IntentType> NAME_MAP;

    public static List<IntentType> valuesList() {
        return VALUES_LIST;
    }

    static {
        Map<String, IntentType> map = new HashMap<>(VALUES.length);
        for (IntentType intentType : VALUES) {
            map.put(intentType.name(), intentType);
        }
        NAME_MAP = Collections.unmodifiableMap(map);
    }

    public static IntentType fromStringOrDefault(String name) {
        if (name == null || name.trim().isEmpty()) {
            log.warn("Received null or empty intent name, defaulting to UNKNOWN");
            return UNKNOWN;
        }
        return NAME_MAP.getOrDefault(name.toUpperCase(), UNKNOWN);
    }

}
