package com.mycompany.bookme.restaurant.service;

import com.mycompany.bookme.exceptions.NotFoundException;
import com.mycompany.bookme.model.RestaurantDTO;
import com.mycompany.bookme.restaurant.dao.models.entities.RestaurantEntity;
import com.mycompany.bookme.restaurant.dao.repositories.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @InjectMocks RestaurantService service;
    @Mock ModelMapper modelMapper;
    @Mock RestaurantRepository repository;

    @Test
    void createRestaurant_ok() {
        RestaurantEntity input = RestaurantEntity.builder()
                .name("Meson Las Casas").address("Calle Falsa 123").phone("600000000")
                .build();

        RestaurantEntity saved = RestaurantEntity.builder()
                .id(1L).name("Meson Las Casas").address("Calle Falsa 123").phone("600000000")
                .build();

        when(repository.save(input)).thenReturn(saved);

        RestaurantEntity result = service.createRestaurant(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(repository, times(1)).save(input);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void getRestaurantById_ok() {
        Long id = 1L;
        RestaurantEntity entity = RestaurantEntity.builder()
                .id(id).name("Meson Las Casas").address("Calle Falsa 123").phone("600000000")
                .build();
        RestaurantDTO dto = new RestaurantDTO();
        dto.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(modelMapper.map(entity, RestaurantDTO.class)).thenReturn(dto);

        RestaurantDTO result = service.getRestaurantById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(repository).findById(id);
        verify(modelMapper).map(entity, RestaurantDTO.class);
        verifyNoMoreInteractions(repository, modelMapper);
    }

    @Test
    void getRestaurantById_notFound_throws() {
        long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.getRestaurantById(id));
        assertTrue(ex.getMessage().contains("Restaurant not found: " + id));

        verify(repository).findById(id);
        verifyNoInteractions(modelMapper);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getRestaurantEntityById_ok() {
        Long id = 2L;
        RestaurantEntity entity = RestaurantEntity.builder().id(id).build();
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        RestaurantEntity result = service.getRestaurantEntityById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(repository).findById(id);
        verifyNoInteractions(modelMapper);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getRestaurantEntityById_notFound_throws() {
        long id = 123L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.getRestaurantEntityById(id));
        assertTrue(ex.getMessage().contains("Restaurant not found: " + id));

        verify(repository).findById(id);
        verifyNoInteractions(modelMapper);
        verifyNoMoreInteractions(repository);
    }
}
