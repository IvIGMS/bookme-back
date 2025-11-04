package com.mycompany.bookme.table.dao.models.entities;

import com.mycompany.bookme.exceptions.utils.AuditableEntity;
import com.mycompany.bookme.reservation.dao.models.entities.ReservationEntity;
import com.mycompany.bookme.restaurant.dao.models.entities.RestaurantEntity;
import com.mycompany.bookme.security.dao.models.entities.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tables", schema = "bookme")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TableEntity extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Integer tableNumber;

  @Column(nullable = false)
  private Integer capacity;

  @ManyToOne
  @JoinColumn(name = "restaurant_id")
  private RestaurantEntity restaurant;

  @OneToMany(mappedBy = "table")
  private List<ReservationEntity> reservationEntities;
}

