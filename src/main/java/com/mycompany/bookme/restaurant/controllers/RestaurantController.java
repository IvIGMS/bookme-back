package com.mycompany.bookme.restaurant.controllers;

import com.mycompany.bookme.api.RestaurantsApi;
import com.mycompany.bookme.exceptions.utils.ControllerUtils;
import com.mycompany.bookme.model.RestaurantDTO;
import com.mycompany.bookme.model.RestaurantRequestDTO;
import com.mycompany.bookme.restaurant.service.RestaurantService;
import com.mycompany.bookme.restaurant.service.RestaurantUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.mycompany.bookme.exceptions.utils.ControllerUtilsConstants.STRING_NO_PREMISSIONS;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class RestaurantController extends ControllerUtils implements RestaurantsApi {
    private final RestaurantService restaurantService;
    private final RestaurantUserService restaurantUserService;

    @Override
    public ResponseEntity<RestaurantDTO> createRestaurant(RestaurantRequestDTO restaurantRequestDTO) {
        if (!checkIsUser()) {
            throw new com.ivanfrias.generic_project.exceptions.utils.UnauthorizedException(STRING_NO_PREMISSIONS);
        }
        Long ownerId = getAllClaims().get("user_id", Long.class);
        return ResponseEntity.created(null).body(
                restaurantUserService.createRestaurant(restaurantRequestDTO, ownerId));
    }
}

