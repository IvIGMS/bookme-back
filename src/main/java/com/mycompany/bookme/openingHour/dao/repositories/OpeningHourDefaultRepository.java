package com.mycompany.bookme.openingHour.dao.repositories;

import com.mycompany.bookme.openingHour.dao.models.entities.OpeningHourDefaultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpeningHourDefaultRepository extends JpaRepository<OpeningHourDefaultEntity, Long> {
}