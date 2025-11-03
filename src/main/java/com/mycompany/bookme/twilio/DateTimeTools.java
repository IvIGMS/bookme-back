package com.mycompany.bookme.twilio;

import org.springframework.ai.tool.annotation.Tool;

import java.time.LocalDateTime;

public class DateTimeTools {


    @Tool(
            name = "get_current_datetime",
            description = "Obtiene la fecha y hora actual en formato ISO 8601."
    )
    public String getCurrentDateTime() {
        return LocalDateTime.now().toString();
    }
}
