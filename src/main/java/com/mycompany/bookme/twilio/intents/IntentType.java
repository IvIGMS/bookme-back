package com.mycompany.bookme.twilio.intents;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public enum IntentType {
    BOOK_TABLE,         // intención inicial de reservar
    CANCEL_RESERVATION,
    ASK_OPENING_HOURS,
    ASK_MENU,
    PROVIDE_TIME,       // el usuario sólo dio la hora
    PROVIDE_DATE,       // el usuario sólo dio el día/fecha
    PROVIDE_PARTY_SIZE, // el usuario sólo dio el nº de personas
    CONFIRMATION,       // sí / no
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
