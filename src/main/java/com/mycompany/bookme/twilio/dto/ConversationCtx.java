package com.mycompany.bookme.twilio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public final class ConversationCtx {
    private LocalDate reservationDate;
    private LocalTime reservationHour;
    private Integer partySize;
    private String currentUserMessage;

    public ConversationCtx(LocalDate reservationDate, LocalTime reservationHour, Integer partySize, String currentUserMessage) {
        this.reservationDate = reservationDate;
        this.reservationHour = reservationHour;
        this.partySize = partySize;
        this.currentUserMessage = currentUserMessage;
    }

    public ConversationCtx(LocalDate reservationDate, LocalTime reservationHour, Integer partySize) {
        this.reservationDate = reservationDate;
        this.reservationHour = reservationHour;
        this.partySize = partySize;
    }


}
