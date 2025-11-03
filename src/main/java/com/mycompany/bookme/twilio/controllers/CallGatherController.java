package com.mycompany.bookme.twilio.controllers;

import com.mycompany.bookme.twilio.utils.TwiMLHelper;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Gather;
import com.twilio.twiml.voice.Say;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.twilio.http.HttpMethod.POST;

@RestController
@RequestMapping("/api/v1/voice/bookings/inquiry")
@Slf4j
@RequiredArgsConstructor
public class CallGatherController {

    @PostMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public String handleIncomingCall(@RequestParam(value = "CallSid", required = false) String callSid) {
        log.info("Llamada recibida con id: {}", callSid);
        final Say saludoInicial = TwiMLHelper.say("Hola, gracias por llamar a Book Me.");

        final Gather gather = new Gather.Builder()
                .say(saludoInicial)
                .inputs(Gather.Input.SPEECH)
                .action("/api/v1/voice/bookings/process-speech")
                .method(POST)
                .build();

        return new VoiceResponse.Builder()
                .gather(gather)
                .build()
                .toXml();
    }

}
