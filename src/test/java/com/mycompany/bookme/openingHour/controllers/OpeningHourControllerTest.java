package com.mycompany.bookme.openingHour.controllers;

import com.mycompany.bookme.model.OpeningHourDefaultCompleteRequestDTO;
import com.mycompany.bookme.model.OpeningHourDefaultCompleteResponseDTO;
import com.mycompany.bookme.openingHour.service.OpeningHourDefaultService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpeningHourControllerTest {

    @InjectMocks
    OpeningHourController openingHourController;

    @Mock
    OpeningHourDefaultService openingHourDefaultService;

    @Test
    void createOpeningHourDefault_returns201() {
        // given
        var req  = new OpeningHourDefaultCompleteRequestDTO();
        var resp = new OpeningHourDefaultCompleteResponseDTO();
        resp.setRestaurantId(1L);

        when(openingHourDefaultService.createOpeningHourDefault(req)).thenReturn(resp);

        // when
        ResponseEntity<OpeningHourDefaultCompleteResponseDTO> response =
                openingHourController.createOpeningHourDefault(req);

        // then
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getRestaurantId());

        verify(openingHourDefaultService).createOpeningHourDefault(req);
        verifyNoMoreInteractions(openingHourDefaultService);
    }

    @Test
    void updateOpeningHourDefault_returns200() {
        // given
        var req  = new OpeningHourDefaultCompleteRequestDTO();
        var resp = new OpeningHourDefaultCompleteResponseDTO();
        resp.setRestaurantId(2L);

        when(openingHourDefaultService.updateOpeningHourDefault(req)).thenReturn(resp);

        // when
        ResponseEntity<OpeningHourDefaultCompleteResponseDTO> response =
                openingHourController.updateOpeningHourDefault(req);

        // then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getRestaurantId());

        verify(openingHourDefaultService).updateOpeningHourDefault(req);
        verifyNoMoreInteractions(openingHourDefaultService);
    }

    @Test
    void getOpeningHoursDefault_returns200() {
        // given
        Long restaurantId = 5L;
        var resp = new OpeningHourDefaultCompleteResponseDTO();
        resp.setRestaurantId(restaurantId);

        when(openingHourDefaultService.getOpeningHoursDefault(restaurantId)).thenReturn(resp);

        // when
        ResponseEntity<OpeningHourDefaultCompleteResponseDTO> response =
                openingHourController.getOpeningHoursDafult(restaurantId);

        // then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(restaurantId, response.getBody().getRestaurantId());

        verify(openingHourDefaultService).getOpeningHoursDefault(restaurantId);
        verifyNoMoreInteractions(openingHourDefaultService);
    }
}
