package com.mycompany.bookme.table.dao.repositories;

import com.mycompany.bookme.table.dao.models.entities.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TableRepository extends JpaRepository<TableEntity, Long> {
    List<TableEntity> findByRestaurant_Id(Long restaurantId);
}