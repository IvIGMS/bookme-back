package com.mycompany.bookme.openingHour.dao.repositories;

import com.mycompany.bookme.openingHour.dao.models.entities.OpeningHourDefaultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OpeningHourDefaultRepository extends JpaRepository<OpeningHourDefaultEntity, Long> {
    List<OpeningHourDefaultEntity> findByRestaurant_Id(Long restaurantId);
    List<OpeningHourDefaultEntity> findByRestaurant_IdOrderByDayOfWeekAscOpeningTimeAsc(Long restaurantId);
    boolean existsByRestaurant_Id(Long restaurantId);
}