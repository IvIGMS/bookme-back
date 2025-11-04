package com.mycompany.bookme.restaurant.service;

import com.mycompany.bookme.exceptions.NotFoundException;
import com.mycompany.bookme.model.RestaurantDTO;
import com.mycompany.bookme.model.RestaurantRequestDTO;
import com.mycompany.bookme.restaurant.dao.models.entities.RestaurantEntity;
import com.mycompany.bookme.restaurant.dao.models.entities.RestaurantUserEntity;
import com.mycompany.bookme.restaurant.dao.repositories.RestaurantRepository;
import com.mycompany.bookme.restaurant.dao.repositories.RestaurantUserRepository;
import com.mycompany.bookme.security.dao.models.entities.UserEntity;
import com.mycompany.bookme.security.dao.models.enums.RoleUserEnum;
import com.mycompany.bookme.security.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantUserService {
    private final RestaurantUserRepository restaurantUserRepository;
    private final RestaurantService restaurantService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Transactional
    public RestaurantDTO createRestaurant(RestaurantRequestDTO restaurantRequestDTO, Long userId) {
        UserEntity userEntity = userService.getUserEntityById(userId);
        RestaurantEntity restaurantEntityToBeSaved = modelMapper.map(restaurantRequestDTO, RestaurantEntity.class);

        // Save restaurant
        RestaurantEntity restaurantEntitySaved = restaurantService.createRestaurant(restaurantEntityToBeSaved);

        // Link restaurant ot user
        restaurantEntitySaved.addUserLink(RestaurantUserEntity.builder()
                .user(userEntity)
                .restaurant(restaurantEntitySaved)
                .role(RoleUserEnum.ADMIN)
                .build());
        // todo: arreglar mapper para la auditoría
        return modelMapper.map(restaurantEntitySaved, RestaurantDTO.class);
    }
}
