package com.mycompany.bookme.twilio.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "twilio.speech")
@Getter
@Setter
public class TwilioProperties {
    private double confidenceThreshold = 0.7; // Valor por defecto
}
