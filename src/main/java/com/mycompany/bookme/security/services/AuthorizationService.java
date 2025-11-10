package com.mycompany.bookme.security.services;

import com.mycompany.bookme.restaurant.dao.repositories.RestaurantUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("authz")
@RequiredArgsConstructor
public class AuthorizationService {
    private final RestaurantUserRepository restaurantUserRepository;

    public boolean canAccessRestaurant(Long restaurantId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return false;

        Object details = authentication.getDetails();
        Long userId = null;
        if (details instanceof java.util.Map<?,?> map && map.get("user_id") != null) {
            Object id = map.get("user_id");
            try {
                userId = (id instanceof Number) ? ((Number) id).longValue() : Long.parseLong(id.toString());
            } catch (NumberFormatException e) {
                userId = null;
            }
        }
        if (userId == null) return false;

        return restaurantUserRepository.existsByIdUserIdAndIdRestaurantId(userId, restaurantId);
    }
}

