package com.mycompany.bookme.security.dao.models.entities;

import com.mycompany.bookme.exceptions.utils.AuditableEntity;
import com.mycompany.bookme.security.dao.models.enums.RoleUserEnum;
import com.mycompany.bookme.restaurant.dao.models.entities.RestaurantEntity;
import com.mycompany.bookme.restaurant.dao.models.entities.RestaurantUserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "users", schema = "bookme")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserEntity extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String firstname;

  @Column(nullable = false)
  private String lastname;

  @Column()
  private String phoneNumber;

  @Builder.Default
  @Column(nullable = false)
  private Boolean isActive = true;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RoleUserEnum role;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<RestaurantUserEntity> restaurantLinks = new ArrayList<>();

  @Transient
  public List<RestaurantEntity> getRestaurants() {
    return restaurantLinks.stream().map(RestaurantUserEntity::getRestaurant).collect(Collectors.toList());
  }

  public void addRestaurantLink(RestaurantUserEntity ru) {
    restaurantLinks.add(ru);
    ru.setUser(this);
  }

  public void removeRestaurantLink(RestaurantUserEntity ru) {
    restaurantLinks.remove(ru);
    ru.setUser(null);
  }
}
