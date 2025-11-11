package com.mycompany.bookme.openingHour.service;

import com.mycompany.bookme.exceptions.ConflictException;
import com.mycompany.bookme.model.OpeningHourDefaultCompleteRequestDTO;
import com.mycompany.bookme.model.OpeningHourDefaultCompleteResponseDTO;
import com.mycompany.bookme.model.OpeningHourDefaultDTO;
import com.mycompany.bookme.model.OpeningHourDefaultRequestDTO;
import com.mycompany.bookme.openingHour.dao.models.entities.OpeningHourDefaultEntity;
import com.mycompany.bookme.openingHour.dao.repositories.OpeningHourDefaultRepository;
import com.mycompany.bookme.restaurant.dao.models.entities.RestaurantEntity;
import com.mycompany.bookme.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OpeningHourDefaultService {
    private final OpeningHourDefaultRepository openingHourRepository;
    private final ModelMapper modelMapper;
    private final RestaurantService restaurantService;

    @Transactional
    @PreAuthorize("@authz.canAccessRestaurant(#req.restaurantId, authentication)")
    public OpeningHourDefaultCompleteResponseDTO createOpeningHourDefault(
            @P("req") OpeningHourDefaultCompleteRequestDTO openingHourDefaultCompleteRequestDTO) {
        if(openingHourRepository.existsByRestaurant_Id(openingHourDefaultCompleteRequestDTO.getRestaurantId())) {
            throw new ConflictException("This restaurant already has an opening hour default");
        }
        validateCreateHours(openingHourDefaultCompleteRequestDTO);
        return createOpeningHours(openingHourDefaultCompleteRequestDTO);
    }

    private OpeningHourDefaultCompleteResponseDTO createOpeningHours(OpeningHourDefaultCompleteRequestDTO openingHourDefaultCompleteRequestDTO) {
        RestaurantEntity restaurantEntity = restaurantService.getRestaurantEntityById(openingHourDefaultCompleteRequestDTO.getRestaurantId());

        List<OpeningHourDefaultEntity> dayListToSave = new ArrayList<>();
        openingHourDefaultCompleteRequestDTO.getOpeningHoursDefault().forEach(day -> {
            OffsetTime openingTime = day.getOpeningTime();
            OffsetTime closingTime = day.getClosingTime();

            OpeningHourDefaultEntity dayToBeSaved = OpeningHourDefaultEntity.builder()
                    .dayOfWeek(day.getDayOfWeek())
                    .openingTime(openingTime)
                    .closingTime(closingTime)
                    .restaurant(restaurantEntity)
                    .build();

            dayListToSave.add(dayToBeSaved);
        });

        List<OpeningHourDefaultEntity> daysSaved = openingHourRepository.saveAll(dayListToSave);

        List<OpeningHourDefaultDTO> dayList = daysSaved.stream()
                .map(day -> modelMapper.map(day, OpeningHourDefaultDTO.class))
                .toList();

        fillClosedDays(dayList, restaurantEntity);

        return OpeningHourDefaultCompleteResponseDTO.builder()
                .openingHoursDefault(dayList)
                .restaurantId(openingHourDefaultCompleteRequestDTO.getRestaurantId())
                .build();
    }

    private OpeningHourDefaultCompleteResponseDTO updateOpeningHours(OpeningHourDefaultCompleteRequestDTO openingHourDefaultCompleteRequestDTO) {
        RestaurantEntity restaurantEntity = restaurantService.getRestaurantEntityById(openingHourDefaultCompleteRequestDTO.getRestaurantId());
        List<OpeningHourDefaultDTO> dayList = new ArrayList<>();

        List<OpeningHourDefaultEntity> foundDays = openingHourRepository.findByRestaurant_Id(restaurantEntity.getId());

        if(foundDays.isEmpty()) {
            throw new ConflictException("No se han encontrado horario por default para este restaurante");
        }

        Map<Integer, OpeningHourDefaultEntity> foundDaysMap = foundDays.stream()
                .collect(Collectors.toMap(
                        OpeningHourDefaultEntity::getDayOfWeek,
                        e -> e
                ));

        openingHourDefaultCompleteRequestDTO.getOpeningHoursDefault().forEach(day -> {

            OpeningHourDefaultEntity dayFound = foundDaysMap.get(day.getDayOfWeek());

            if(Objects.isNull(dayFound)) {
                throw new ConflictException("El día " + day.getDayOfWeek() + " no fue creado para este restaurante");
            }

            // Se quiere dejar ese día como cerrado
            if(Objects.isNull(day.getOpeningTime()) && Objects.isNull(day.getClosingTime())) {
                dayFound.setOpeningTime(null);
                dayFound.setClosingTime(null);

                // Se añade al result de cambios
                dayList.add(modelMapper.map(dayFound, OpeningHourDefaultDTO.class));
            // Se quiere cambiar el horario para ese día.
            } else {
                OffsetTime openingTime = day.getOpeningTime();
                OffsetTime closingTime = day.getClosingTime();

                dayFound.setOpeningTime(openingTime);
                dayFound.setClosingTime(closingTime);

                // Se añade al result de cambios
                dayList.add(modelMapper.map(dayFound, OpeningHourDefaultDTO.class));
            }
        });

        return OpeningHourDefaultCompleteResponseDTO.builder()
                .openingHoursDefault(dayList)
                .restaurantId(openingHourDefaultCompleteRequestDTO.getRestaurantId())
                .build();
    }

    private void fillClosedDays(List<OpeningHourDefaultDTO> dayList, RestaurantEntity restaurantEntity) {
        List<Integer> daysFilled = dayList.stream().map(OpeningHourDefaultDTO::getDayOfWeek).toList();

        List<Integer> emptyDays = Stream.of(0,1,2,3,4,5,6)
                        .filter(id -> !daysFilled.contains(id))
                        .toList();

        List<OpeningHourDefaultEntity> emptyDaysToSave = new ArrayList<>();
        emptyDays.forEach(day -> {
            OpeningHourDefaultEntity emptyDay = OpeningHourDefaultEntity.builder()
                    .dayOfWeek(day)
                    .restaurant(restaurantEntity)
                    .build();
            emptyDaysToSave.add(emptyDay);
        });

        openingHourRepository.saveAll(emptyDaysToSave);
    }

    private void validateCreateHours(OpeningHourDefaultCompleteRequestDTO dto) {
        dto.getOpeningHoursDefault().forEach(this::validateCreateDay);
    }

    private void validateUpdateHours(OpeningHourDefaultCompleteRequestDTO dto) {
        dto.getOpeningHoursDefault().forEach(this::validateUpdateDay);
    }

    private void validateCreateDay(OpeningHourDefaultRequestDTO dayDto) {
        if (dayDto != null) {
            validateTime(dayDto.getOpeningTime());
            validateTime(dayDto.getClosingTime());
        }
    }

    private void validateUpdateDay(OpeningHourDefaultRequestDTO dayDto) {
        if (dayDto == null) return;

        OffsetTime open = dayDto.getOpeningTime();
        OffsetTime close = dayDto.getClosingTime();

        if (open != null && close != null) {
            validateTime(open);
            validateTime(close);
        }

        if ((open != null && close == null) || (open == null && close != null)) {
            throw new ConflictException("El open y close deben estar los dos rellenos o los dos vacíos");
        }
    }

    private void validateTime(OffsetTime value) {
        if (value == null) {
            throw new IllegalArgumentException("La hora es obligatoria para la creación si se le pasa el día.");
        }
    }

    @Transactional
    @PreAuthorize("@authz.canAccessRestaurant(#req.restaurantId, authentication)")
    public OpeningHourDefaultCompleteResponseDTO updateOpeningHourDefault(
            @P("req") OpeningHourDefaultCompleteRequestDTO openingHourDefaultCompleteRequestDTO) {
        validateUpdateHours(openingHourDefaultCompleteRequestDTO);
        return updateOpeningHours(openingHourDefaultCompleteRequestDTO);
    }

    @PreAuthorize("@authz.canAccessRestaurant(#restaurantId, authentication)")
    public OpeningHourDefaultCompleteResponseDTO getOpeningHoursDefault(Long restaurantId) {
        List<OpeningHourDefaultEntity> openingHourDefaultEntities =
                openingHourRepository.findByRestaurant_IdOrderByDayOfWeekAscOpeningTimeAsc(restaurantId);

        List<OpeningHourDefaultDTO> openingHourDefaultDTOList = openingHourDefaultEntities.stream()
                .map(day -> modelMapper.map(day, OpeningHourDefaultDTO.class))
                .toList();

        return OpeningHourDefaultCompleteResponseDTO.builder()
                .restaurantId(restaurantId)
                .openingHoursDefault(openingHourDefaultDTOList)
                .build();
    }
}
