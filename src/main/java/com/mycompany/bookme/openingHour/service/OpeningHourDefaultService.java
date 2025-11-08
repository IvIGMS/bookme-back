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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OpeningHourDefaultService {
    private final OpeningHourDefaultRepository openingHourRepository;
    private final ModelMapper modelMapper;
    private final RestaurantService restaurantService;


    private static final Pattern TIME_PATTERN = Pattern.compile("^(?:[01]\\d|2[0-3]):[0-5]\\d$");
    private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");

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
        List<OpeningHourDefaultDTO> dayList = new ArrayList<>();
        openingHourDefaultCompleteRequestDTO.getOpeningHoursDefault().forEach(day -> {
            LocalTime openingTime = toLocalTime(day.getOpeningTime());
            LocalTime closingTime = toLocalTime(day.getClosingTime());
            boolean closeNextDay = closeNextDay(openingTime, closingTime);

            OpeningHourDefaultEntity dayToBeSaved = OpeningHourDefaultEntity.builder()
                    .dayOfWeek(day.getDayOfWeek())
                    .openingTime(openingTime)
                    .closingTime(closingTime)
                    .closeNextDay(closeNextDay)
                    .restaurant(restaurantEntity)
                    .build();

            OpeningHourDefaultEntity daySaved = openingHourRepository.save(dayToBeSaved);
            dayList.add(modelMapper.map(daySaved, OpeningHourDefaultDTO.class));
        });

        fillClosedDays(dayList, restaurantEntity);

        return OpeningHourDefaultCompleteResponseDTO.builder()
                .openingHoursDefault(dayList)
                .restaurantId(openingHourDefaultCompleteRequestDTO.getRestaurantId())
                .build();
    }

    private OpeningHourDefaultCompleteResponseDTO updateOpeningHours(OpeningHourDefaultCompleteRequestDTO openingHourDefaultCompleteRequestDTO) {
        RestaurantEntity restaurantEntity = restaurantService.getRestaurantEntityById(openingHourDefaultCompleteRequestDTO.getRestaurantId());
        List<OpeningHourDefaultDTO> dayList = new ArrayList<>();
        openingHourDefaultCompleteRequestDTO.getOpeningHoursDefault().forEach(day -> {

            OpeningHourDefaultEntity openingHourDefaultEntity = openingHourRepository.findByRestaurant_IdAndDayOfWeek(restaurantEntity.getId(), day.getDayOfWeek())
                    .orElseThrow(() -> new ConflictException("The day " + day.getDayOfWeek() + " has not been created before the put"));

            // Se quiere dejar ese día como cerrado
            if(Objects.isNull(day.getOpeningTime()) && Objects.isNull(day.getClosingTime())) {
                openingHourDefaultEntity.setOpeningTime(null);
                openingHourDefaultEntity.setClosingTime(null);
                openingHourDefaultEntity.setCloseNextDay(false);

                // Se añade al result de cambios
                dayList.add(modelMapper.map(openingHourDefaultEntity, OpeningHourDefaultDTO.class));
            // Se quiere cambiar el horario para ese día.
            } else {
                LocalTime openingTime = toLocalTime(day.getOpeningTime());
                LocalTime closingTime = toLocalTime(day.getClosingTime());
                boolean closeNextDay = closeNextDay(openingTime, closingTime);

                openingHourDefaultEntity.setOpeningTime(openingTime);
                openingHourDefaultEntity.setClosingTime(closingTime);
                openingHourDefaultEntity.setCloseNextDay(closeNextDay);

                // Se añade al result de cambios
                dayList.add(modelMapper.map(openingHourDefaultEntity, OpeningHourDefaultDTO.class));
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

        emptyDays.forEach(day -> {
            OpeningHourDefaultEntity emptyDay = OpeningHourDefaultEntity.builder()
                    .dayOfWeek(day)
                    .closeNextDay(false)
                    .restaurant(restaurantEntity)
                    .build();
            openingHourRepository.save(emptyDay);
        });
    }

    private boolean closeNextDay(LocalTime openingTime, LocalTime closingTime) {
        return openingTime.isAfter(closingTime);
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

        String open = dayDto.getOpeningTime();
        String close = dayDto.getClosingTime();

        if (open != null && close != null) {
            validateTime(open);
            validateTime(close);
        }

        if ((open != null && close == null) || (open == null && close != null)) {
            throw new ConflictException("El open y close deben estár los dos rellenos o los dos vacíos");
        }
    }

    private void validateTime(String value) {
        if (value == null) {
            throw new IllegalArgumentException("La hora es obligatoria para la creación si se le pasa el día.");
        }
        if (!TIME_PATTERN.matcher(value).matches()) {
            throw new ConflictException("La hora debe cumplir el patrón HH:mm (p.ej. 13:00)");
        }
    }

    private LocalTime toLocalTime(String hhmm) {
        return LocalTime.parse(hhmm, HH_MM);
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
