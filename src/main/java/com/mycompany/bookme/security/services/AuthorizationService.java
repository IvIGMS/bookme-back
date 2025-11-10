package com.mycompany.bookme.security.services;

import com.mycompany.bookme.restaurant.dao.repositories.RestaurantUserRepository;
import com.mycompany.bookme.table.dao.models.entities.TableEntity;
import com.mycompany.bookme.table.service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("authz")
@RequiredArgsConstructor
public class AuthorizationService {
    private final RestaurantUserRepository restaurantUserRepository;
    private final TableService tableService;


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

    public boolean canAccessTable(Long tableId, Authentication authentication) {
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

        TableEntity table = tableService.getTableEntityById(tableId);
        return restaurantUserRepository.existsByIdUserIdAndIdRestaurantId(userId, table.getRestaurant().getId());
    }
}

