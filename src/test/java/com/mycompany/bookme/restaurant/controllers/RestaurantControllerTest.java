package com.mycompany.bookme.restaurant.controllers;

import com.mycompany.bookme.model.RestaurantDTO;
import com.mycompany.bookme.model.RestaurantRequestDTO;
import com.mycompany.bookme.restaurant.service.RestaurantService;
import com.mycompany.bookme.restaurant.service.RestaurantUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantControllerTest {

    @Mock RestaurantService restaurantService;
    @Mock RestaurantUserService restaurantUserService;

    @Test
    void createRestaurant_ok_returns201_andCallsServiceWithOwnerId() {
        // given
        long expectedOwnerId = 77L;
        RestaurantRequestDTO req = new RestaurantRequestDTO();
        RestaurantDTO dto = new RestaurantDTO();
        dto.setId(1L);

        when(restaurantUserService.createRestaurant(any(RestaurantRequestDTO.class), eq(expectedOwnerId)))
                .thenReturn(dto);

        // controlador con overrides mínimos (sin spy)
        RestaurantController controller = new RestaurantController(restaurantService, restaurantUserService) {
            @Override protected boolean checkIsUser() { return true; }
            @Override protected Long getUserIdClaim() { return expectedOwnerId; }
        };

        // when
        ResponseEntity<RestaurantDTO> response = controller.createRestaurant(req);

        // then
        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());

        verify(restaurantUserService).createRestaurant(req, expectedOwnerId);
        verifyNoMoreInteractions(restaurantUserService, restaurantService);
    }

    @Test
    void createRestaurant_unauthorized_throws() {
        // controlador que simula no ser USER
        RestaurantController controller = new RestaurantController(restaurantService, restaurantUserService) {
            @Override protected boolean checkIsUser() { return false; }
        };

        // then
        assertThrows(
                com.ivanfrias.generic_project.exceptions.utils.UnauthorizedException.class,
                () -> controller.createRestaurant(new RestaurantRequestDTO())
        );

        verifyNoInteractions(restaurantUserService, restaurantService);
    }

    @Test
    void getRestaurantById_ok_returns200() {
        // given
        long restaurantId = 5L;
        RestaurantDTO dto = new RestaurantDTO();
        dto.setId(restaurantId);

        when(restaurantService.getRestaurantById(restaurantId)).thenReturn(dto);

        RestaurantController controller = new RestaurantController(restaurantService, restaurantUserService) { };

        // when
        ResponseEntity<RestaurantDTO> response = controller.getRestaurantById(restaurantId);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(restaurantId, response.getBody().getId());

        verify(restaurantService).getRestaurantById(restaurantId);
        verifyNoMoreInteractions(restaurantService);
        verifyNoInteractions(restaurantUserService);
    }
}
