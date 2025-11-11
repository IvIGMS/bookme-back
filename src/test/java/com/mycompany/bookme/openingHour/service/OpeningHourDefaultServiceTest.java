package com.mycompany.bookme.openingHour.service;

import com.mycompany.bookme.exceptions.ConflictException;
import com.mycompany.bookme.model.OpeningHourDefaultCompleteRequestDTO;
import com.mycompany.bookme.model.OpeningHourDefaultDTO;
import com.mycompany.bookme.model.OpeningHourDefaultRequestDTO;
import com.mycompany.bookme.openingHour.dao.models.entities.OpeningHourDefaultEntity;
import com.mycompany.bookme.openingHour.dao.repositories.OpeningHourDefaultRepository;
import com.mycompany.bookme.restaurant.dao.models.entities.RestaurantEntity;
import com.mycompany.bookme.restaurant.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpeningHourDefaultServiceTest {
    @InjectMocks OpeningHourDefaultService service;
    @Mock ModelMapper modelMapper;
    @Mock OpeningHourDefaultRepository repository;
    @Mock RestaurantService restaurantService;

    @Test
    void testCreateOpeningHourDefault_ok() {
        var requestDTO = createRequest(false);
        RestaurantEntity restaurant = RestaurantEntity.builder().build();

        OpeningHourDefaultDTO openingHourDefaultDTO = OpeningHourDefaultDTO.builder()
                .dayOfWeek(0)
                .openingTime(OffsetTime.of(LocalTime.of(18, 30), ZoneOffset.UTC))
                .closingTime(OffsetTime.of(LocalTime.of(23, 30), ZoneOffset.UTC))
                .build();

        OpeningHourDefaultEntity dayToSave = OpeningHourDefaultEntity.builder().build();

        when(repository.existsByRestaurant_Id(1L))
                .thenReturn(false);
        when(restaurantService.getRestaurantEntityById(1L))
                .thenReturn(restaurant);
        when(repository.saveAll(any()))
                .thenReturn(List.of(dayToSave));
        when(modelMapper.map(any(), eq(OpeningHourDefaultDTO.class)))
                .thenReturn(openingHourDefaultDTO);

        var results = service.createOpeningHourDefault(requestDTO);
        assertNotNull(results);
    }

    @Test
    void testCreateOpeningHourDefault_ko_ExistOpeningHoursAlready() {
        var requestDTO = OpeningHourDefaultCompleteRequestDTO.builder()
                .openingHoursDefault(List.of(OpeningHourDefaultRequestDTO.builder().build()))
                .restaurantId(1L)
                .build();
        when(repository.existsByRestaurant_Id(1L))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> service.createOpeningHourDefault(requestDTO));
    }

    @Test
    void testCreateOpeningHourDefault_ko_anHourIsNull() {
        OpeningHourDefaultCompleteRequestDTO requestDTO = OpeningHourDefaultCompleteRequestDTO.builder()
                .restaurantId(1L)
                .openingHoursDefault(List.of(OpeningHourDefaultRequestDTO.builder()
                        .dayOfWeek(1)
                        .build()))
                .build();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createOpeningHourDefault(requestDTO));
    }

    private OpeningHourDefaultCompleteRequestDTO createRequest(boolean update) {
        List<OpeningHourDefaultRequestDTO> list = new ArrayList<>();

        OpeningHourDefaultRequestDTO uno = OpeningHourDefaultRequestDTO.builder()
                .dayOfWeek(0)
                .openingTime(OffsetTime.of(LocalTime.of(19, 30), ZoneOffset.UTC))
                .closingTime(OffsetTime.of(LocalTime.of(23, 30), ZoneOffset.UTC))
                .build();

        OpeningHourDefaultRequestDTO dos = OpeningHourDefaultRequestDTO.builder()
                .dayOfWeek(6)
                .openingTime(OffsetTime.of(LocalTime.of(20, 30), ZoneOffset.UTC))
                .closingTime(OffsetTime.of(LocalTime.of(0, 30), ZoneOffset.UTC))
                .build();

        OpeningHourDefaultRequestDTO tres = OpeningHourDefaultRequestDTO.builder()
                .dayOfWeek(3)
                .build();

        list.add(uno);
        list.add(dos);

        if(update) {
            list.add(tres);
        }

        return OpeningHourDefaultCompleteRequestDTO.builder()
                .openingHoursDefault(list)
                .restaurantId(1L)
                .build();
    }

    @Test
    void testGetOpeningHoursDefault() {
        OpeningHourDefaultDTO openingHourDefaultDTO = OpeningHourDefaultDTO.builder()
                .dayOfWeek(0)
                .openingTime(OffsetTime.of(LocalTime.of(18,30), ZoneOffset.UTC))
                .closingTime(OffsetTime.of(LocalTime.of(23,30), ZoneOffset.UTC))
                .build();

        when(repository.findByRestaurant_IdOrderByDayOfWeekAscOpeningTimeAsc(1L))
                .thenReturn(getOpeningHoursDefault());

        when(modelMapper.map(any(), eq(OpeningHourDefaultDTO.class)))
                .thenReturn(openingHourDefaultDTO);

        var results = service.getOpeningHoursDefault(1L);

        assertNotNull(results);
    }

    public List<OpeningHourDefaultEntity> getOpeningHoursDefault() {
        return List.of(
                OpeningHourDefaultEntity.builder()
                        .id(1L)
                        .dayOfWeek(0)
                        .openingTime(OffsetTime.of(LocalTime.of(18, 30), ZoneOffset.UTC))
                        .closingTime(OffsetTime.of(LocalTime.of(23, 30), ZoneOffset.UTC))
                        .restaurant(RestaurantEntity.builder().id(1L).build())
                        .build(),
                OpeningHourDefaultEntity.builder()
                        .id(2L)
                        .dayOfWeek(0)
                        .openingTime(OffsetTime.of(LocalTime.of(18, 30), ZoneOffset.UTC))
                        .closingTime(OffsetTime.of(LocalTime.of(0, 30), ZoneOffset.UTC))
                        .restaurant(RestaurantEntity.builder().id(1L).build())
                        .build()
        );
    }

    @Test
    void testUpdateOpeningHourDefault_ok() {
        var requestDTO = createRequest(true);

        RestaurantEntity restaurant = RestaurantEntity.builder().build();

        OpeningHourDefaultEntity openingHourDefaultEntityOne = OpeningHourDefaultEntity.builder()
                .id(1L)
                .dayOfWeek(0)
                .openingTime(OffsetTime.of(LocalTime.of(18, 30), ZoneOffset.UTC))
                .closingTime(OffsetTime.of(LocalTime.of(23, 30), ZoneOffset.UTC))
                .build();

        OpeningHourDefaultEntity openingHourDefaultEntityTwo = OpeningHourDefaultEntity.builder()
                .id(2L)
                .dayOfWeek(6)
                .openingTime(OffsetTime.of(LocalTime.of(18, 30), ZoneOffset.UTC))
                .closingTime(OffsetTime.of(LocalTime.of(0, 30), ZoneOffset.UTC))
                .build();

        OpeningHourDefaultEntity openingHourDefaultEntityThree = OpeningHourDefaultEntity.builder()
                .id(2L)
                .dayOfWeek(3)
                .openingTime(OffsetTime.of(LocalTime.of(18, 30), ZoneOffset.UTC))
                .closingTime(OffsetTime.of(LocalTime.of(0, 30), ZoneOffset.UTC))
                .build();

        List<OpeningHourDefaultEntity> openingHourDefaultEntityList = List.of(openingHourDefaultEntityOne, openingHourDefaultEntityTwo, openingHourDefaultEntityThree);

        OpeningHourDefaultDTO openingHourDefaultDTO = OpeningHourDefaultDTO.builder()
                .dayOfWeek(1)
                .openingTime(OffsetTime.of(LocalTime.of(18, 30), ZoneOffset.UTC))
                .closingTime(OffsetTime.of(LocalTime.of(23, 30), ZoneOffset.UTC))
                .build();

        when(restaurantService.getRestaurantEntityById(1L))
                .thenReturn(restaurant);
        when(repository.findByRestaurant_Id(any()))
                .thenReturn(openingHourDefaultEntityList);
        when(modelMapper.map(any(), eq(OpeningHourDefaultDTO.class)))
                .thenReturn(openingHourDefaultDTO);

        var results = service.updateOpeningHourDefault(requestDTO);
        assertNotNull(results);
    }

    @Test
    void testUpdateOpeningHourDefault_ko_noDefaultOpeningHour() {
        var requestDTO = createRequest(true);

        RestaurantEntity restaurant = RestaurantEntity.builder().build();


        List<OpeningHourDefaultEntity> openingHourDefaultEntityList = List.of();

        when(restaurantService.getRestaurantEntityById(1L))
                .thenReturn(restaurant);
        when(repository.findByRestaurant_Id(any()))
                .thenReturn(openingHourDefaultEntityList);

        assertThrows(
                ConflictException.class,
                () -> service.updateOpeningHourDefault(requestDTO));
    }

    @Test
    void testUpdateOpeningHourDefault_ko_aDayIsNotCreatedBefore() {
        var requestDTO = createRequest(true);

        RestaurantEntity restaurant = RestaurantEntity.builder().build();

        OpeningHourDefaultEntity openingHourDefaultEntityOne = OpeningHourDefaultEntity.builder()
                .id(1L)
                .dayOfWeek(1)
                .openingTime(OffsetTime.of(LocalTime.of(18, 30), ZoneOffset.UTC))
                .closingTime(OffsetTime.of(LocalTime.of(23, 30), ZoneOffset.UTC))
                .build();

        List<OpeningHourDefaultEntity> openingHourDefaultEntityList = List.of(openingHourDefaultEntityOne);

        when(restaurantService.getRestaurantEntityById(1L))
                .thenReturn(restaurant);
        when(repository.findByRestaurant_Id(any()))
                .thenReturn(openingHourDefaultEntityList);

        assertThrows(
                ConflictException.class,
                () -> service.updateOpeningHourDefault(requestDTO));
    }

    @Test
    void testUpdateOpeningHourDefault_ko_oneHourIsNullAndOtherIsNotNull() {
        OpeningHourDefaultCompleteRequestDTO requestDTO = OpeningHourDefaultCompleteRequestDTO.builder()
                .restaurantId(1L)
                .openingHoursDefault(List.of(OpeningHourDefaultRequestDTO.builder()
                                .openingTime(OffsetTime.of(LocalTime.of(18,30), ZoneOffset.UTC))
                                .dayOfWeek(1)
                        .build()))
                .build();

        assertThrows(
                ConflictException.class,
                () -> service.updateOpeningHourDefault(requestDTO));
    }
}