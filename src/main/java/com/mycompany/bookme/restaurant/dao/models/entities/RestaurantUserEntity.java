package com.mycompany.bookme.restaurant.dao.models.entities;

import com.mycompany.bookme.exceptions.utils.AuditableEntity;
import com.mycompany.bookme.security.dao.models.entities.UserEntity;
import com.mycompany.bookme.security.dao.models.enums.RoleUserEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restaurant_user", schema = "bookme")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", nullable = false)),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at", nullable = false))
})
public class RestaurantUserEntity extends AuditableEntity {

    @EmbeddedId
    @Builder.Default
    private RestaurantUserId id = new RestaurantUserId(); // ✅ evita el null del embeddable

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("restaurantId")
    @JoinColumn(name = "restaurant_id", nullable = false)
    private RestaurantEntity restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RoleUserEnum role;
}
