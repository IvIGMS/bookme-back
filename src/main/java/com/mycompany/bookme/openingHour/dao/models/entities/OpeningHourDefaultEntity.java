package com.mycompany.bookme.openingHour.dao.models.entities;

import com.mycompany.bookme.exceptions.utils.AuditableEntity;
import com.mycompany.bookme.restaurant.dao.models.entities.RestaurantEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(
        name = "opening_hours_default",
        schema = "bookme",
        indexes = {
                @Index(name = "idx_ohd_restaurant_day", columnList = "restaurant_id, day_of_week")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpeningHourDefaultEntity extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "day_of_week", nullable = false)
  private Integer dayOfWeek; // 1=Lunes (Monday) ... 0=Domingo (Sunday)

  @Column(name = "opening_time", nullable = false)
  private LocalTime openingTime;

  @Column(name = "closing_time")
  private LocalTime closingTime;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "restaurant_id", nullable = false)
  private RestaurantEntity restaurant;
}