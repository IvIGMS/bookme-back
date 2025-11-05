package com.mycompany.bookme.twilio.utils;

import com.mycompany.bookme.twilio.dto.ConversationCtx;

import static java.util.Objects.isNull;

public class ConversationUtils {

    public static String getNextQuestionOrConfirmation(ConversationCtx ctx) {
        if (isNull(ctx.getReservationDate())) {
            return "¿Para qué fecha desea la reserva?";
        } else if (isNull(ctx.getReservationHour())) {
            return "¿A qué hora le gustaría reservar la mesa?";
        } else if (isNull(ctx.getPartySize())) {
            return "¿Para cuántas personas sería la reserva?";
        } else {
            return String.format(
                    "Entonces, desea reservar una mesa para el %s a las %s, para %d personas. ¿Es correcto?",
                    ctx.getReservationDate(),
                    ctx.getReservationHour(),
                    ctx.getPartySize());
        }
    }
}