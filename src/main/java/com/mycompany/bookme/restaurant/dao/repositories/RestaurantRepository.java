package com.mycompany.bookme.restaurant.dao.repositories;

import com.mycompany.bookme.restaurant.dao.models.entities.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {
}