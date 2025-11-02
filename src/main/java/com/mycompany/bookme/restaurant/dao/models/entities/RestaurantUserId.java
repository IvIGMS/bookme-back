package com.mycompany.bookme.restaurant.dao.models.entities;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
public class RestaurantUserId implements Serializable {
    private Long restaurantId;
    private Long userId;
}
