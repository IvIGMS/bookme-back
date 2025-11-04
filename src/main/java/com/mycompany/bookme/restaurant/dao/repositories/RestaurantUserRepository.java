package com.mycompany.bookme.restaurant.dao.repositories;


import com.mycompany.bookme.restaurant.dao.models.entities.RestaurantUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantUserRepository extends JpaRepository<RestaurantUserEntity, Long> {
}
