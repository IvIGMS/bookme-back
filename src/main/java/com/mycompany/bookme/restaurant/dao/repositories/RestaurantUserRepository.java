package com.mycompany.bookme.restaurant.dao.repositories;


import com.mycompany.bookme.restaurant.dao.models.entities.RestaurantUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantUserRepository extends JpaRepository<RestaurantUserEntity, Long> {
    Optional<RestaurantUserEntity> findByIdUserIdAndIdRestaurantId(Long userId, Long restaurantId);
}
