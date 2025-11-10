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
  private Integer dayOfWeek; // ISO-8601: 1=Lunes (Monday) ... 7=Domingo (Sunday)

  @Column(name = "opening_time", nullable = false)
  private LocalTime openingTime;

  @Column(name = "closing_time")
  private LocalTime closingTime;

  @Column(name = "close_next_day")
  private boolean closeNextDay; // true si cierra tras medianoche (ej. 22:00-02:00)

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "restaurant_id", nullable = false)
  private RestaurantEntity restaurant;
}