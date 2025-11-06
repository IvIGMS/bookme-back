package com.mycompany.bookme.restaurant.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.mycompany.bookme.exceptions.NotFoundException;
import com.mycompany.bookme.model.RestaurantDTO;
import com.mycompany.bookme.restaurant.dao.models.entities.RestaurantEntity;
import com.mycompany.bookme.restaurant.dao.repositories.RestaurantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final ModelMapper modelMapper;

    public RestaurantEntity createRestaurant(RestaurantEntity restaurantEntity) {
        return restaurantRepository.save(restaurantEntity);
    }

    public RestaurantDTO getRestaurantById(Long restaurantId) {
        RestaurantEntity restaurantEntity = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant not found: " + restaurantId));
        return modelMapper.map(restaurantEntity, RestaurantDTO.class);
    }

    public RestaurantEntity getRestaurantEntityById(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant not found: " + restaurantId));
    }
}
