package com.mycompany.bookme.openingHour.dao.models.entities;

import com.mycompany.bookme.exceptions.utils.AuditableEntity;
import com.mycompany.bookme.restaurant.dao.models.entities.RestaurantEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetTime;

@Entity
@Table(name = "openingHours", schema = "bookme")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OpeningHourEntity extends AuditableEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Integer dayOfWeek;

  @Column(nullable = false)
  private OffsetTime openingTime;

  @Column(nullable = false)
  private OffsetTime closingTime;

  @ManyToOne
  @JoinColumn(name = "restaurant_id", nullable = false)
  private RestaurantEntity restaurant;
}

