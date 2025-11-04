package com.mycompany.bookme.twilio.controllers;

import com.mycompany.bookme.twilio.services.SpeechProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/voice/bookings/process-speech")
@Slf4j
@RequiredArgsConstructor
public class SpeechProcessController {

    private final SpeechProcessService speechProcessService;

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public String processSpeechResult(
            @RequestParam(value = "SpeechResult", required = false) String speechResult,
            @RequestParam(value = "Confidence", required = false) String confidenceStr,
            @RequestParam(value = "CallSid", required = false) String callSid) {
        return speechProcessService.processSpeechResult(speechResult, confidenceStr, callSid);
    }
}
