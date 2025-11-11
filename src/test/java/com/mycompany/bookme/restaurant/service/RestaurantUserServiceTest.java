package com.mycompany.bookme.restaurant.service;

import com.mycompany.bookme.model.RestaurantDTO;
import com.mycompany.bookme.model.RestaurantRequestDTO;
import com.mycompany.bookme.restaurant.dao.models.entities.RestaurantEntity;
import com.mycompany.bookme.restaurant.dao.repositories.RestaurantUserRepository;
import com.mycompany.bookme.security.dao.models.entities.UserEntity;
import com.mycompany.bookme.security.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantUserServiceTest {

    @InjectMocks
    RestaurantUserService service;

    @Mock
    ModelMapper modelMapper;

    @Mock
    UserService userService;

    @Mock
    RestaurantService restaurantService;

    @Mock
    RestaurantUserRepository repository;

    @Test
    void createRestaurant_ok() {
        // given
        Long userId = 10L;
        var req = new RestaurantRequestDTO();

        var user = UserEntity.builder().id(userId).build();
        var toSave = RestaurantEntity.builder().name("Mesón").build();
        var saved  = RestaurantEntity.builder().id(1L).name("Mesón").build();
        var dto    = new RestaurantDTO();
        dto.setId(1L);

        when(userService.getUserEntityById(userId)).thenReturn(user);
        when(modelMapper.map(req, RestaurantEntity.class)).thenReturn(toSave);
        when(restaurantService.createRestaurant(toSave)).thenReturn(saved);
        when(modelMapper.map(saved, RestaurantDTO.class)).thenReturn(dto);

        // when
        RestaurantDTO result = service.createRestaurant(req, userId);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(userService).getUserEntityById(userId);
        verify(modelMapper).map(req, RestaurantEntity.class);
        verify(restaurantService).createRestaurant(toSave);
        verify(modelMapper).map(saved, RestaurantDTO.class);
        verifyNoMoreInteractions(userService, modelMapper, restaurantService);
    }

    @Test
    void createRestaurant_minimalMapping_ok() {
        // given
        Long userId = 5L;
        var req = new RestaurantRequestDTO();

        when(userService.getUserEntityById(userId)).thenReturn(UserEntity.builder().id(userId).build());
        when(modelMapper.map(req, RestaurantEntity.class)).thenReturn(new RestaurantEntity());
        when(restaurantService.createRestaurant(any(RestaurantEntity.class)))
                .thenReturn(RestaurantEntity.builder().id(42L).build());

        var dto = new RestaurantDTO();
        dto.setId(42L);
        when(modelMapper.map(any(RestaurantEntity.class), eq(RestaurantDTO.class))).thenReturn(dto);

        // when
        var result = service.createRestaurant(req, userId);

        // then
        assertEquals(42L, result.getId());
    }
}
