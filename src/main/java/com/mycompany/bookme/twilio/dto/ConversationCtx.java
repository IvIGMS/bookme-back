package com.mycompany.bookme.twilio.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConversationCtx(
        LocalDate reservationDate,
        LocalTime reservationHour,
        Integer partySize
) {

    public ConversationCtx() {
        this(null, null, null);
    }

}
