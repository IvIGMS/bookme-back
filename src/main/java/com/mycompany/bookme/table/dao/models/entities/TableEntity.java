package com.mycompany.bookme.table.dao.models.entities;

import java.util.List;

import com.mycompany.bookme.exceptions.utils.AuditableEntity;
import com.mycompany.bookme.reservation.dao.models.entities.ReservationEntity;
import com.mycompany.bookme.restaurant.dao.models.entities.RestaurantEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

