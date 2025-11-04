package com.mycompany.bookme.restaurant.dao.models.entities;

import com.mycompany.bookme.exceptions.utils.AuditableEntity;
import com.mycompany.bookme.table.dao.models.entities.TableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "restaurants", schema = "bookme")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RestaurantEntity extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String address;

  @Column(nullable = false)
  private String phone;

  @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<RestaurantUserEntity> userLinks = new ArrayList<>();

  @OneToMany(mappedBy = "restaurant")
  private List<TableEntity> tableEntities;

  // Conveniencia: obtener usuarios directamente (solo lectura)
  @Transient
  public List<com.mycompany.bookme.security.dao.models.entities.UserEntity> getUsers() {
    return userLinks.stream().map(RestaurantUserEntity::getUser).collect(Collectors.toList());
  }

  public void addUserLink(RestaurantUserEntity ru) {
    userLinks.add(ru);
    ru.setRestaurant(this); // sincroniza el otro lado
  }

  public void removeUserLink(RestaurantUserEntity ru) {
    userLinks.remove(ru);
    ru.setRestaurant(null);
  }
}
