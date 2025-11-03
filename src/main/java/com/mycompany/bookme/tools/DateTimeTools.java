package com.mycompany.bookme.tools;

import java.time.LocalDateTime;

import org.springframework.ai.tool.annotation.Tool;

public class DateTimeTools {

    @Tool(
            name = "get_current_datetime",
            description = "Obtiene la fecha y hora actual en formato ISO 8601."
    )
    public String getCurrentDateTime() {
        return LocalDateTime.now().toString();
    }
}
