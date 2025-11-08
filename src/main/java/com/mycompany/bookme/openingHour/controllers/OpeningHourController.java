package com.mycompany.bookme.openingHour.controllers;

import com.mycompany.bookme.api.OpeningHoursApi;
import com.mycompany.bookme.exceptions.utils.ControllerUtils;
import com.mycompany.bookme.model.OpeningHourDefaultCompleteRequestDTO;
import com.mycompany.bookme.model.OpeningHourDefaultCompleteResponseDTO;
import com.mycompany.bookme.model.OpeningHourDefaultDTO;
import com.mycompany.bookme.model.OpeningHourDefaultRequestDTO;
import com.mycompany.bookme.openingHour.service.OpeningHourDefaultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class OpeningHourController extends ControllerUtils implements OpeningHoursApi {
    private final OpeningHourDefaultService openingHourDefaultService;

    @Override
    public ResponseEntity<OpeningHourDefaultCompleteResponseDTO> createOpeningHourDefault(OpeningHourDefaultCompleteRequestDTO openingHourDefaultRequestDTO) {
        return ResponseEntity.created(null)
                .body(openingHourDefaultService.createOpeningHourDefault(openingHourDefaultRequestDTO));
    }
}

