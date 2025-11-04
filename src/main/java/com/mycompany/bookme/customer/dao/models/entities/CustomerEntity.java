package com.mycompany.bookme.customer.dao.models.entities;

import com.mycompany.bookme.exceptions.utils.AuditableEntity;
import com.mycompany.bookme.openingHour.dao.models.entities.OpeningHourEntity;
import com.mycompany.bookme.reservation.dao.models.entities.ReservationEntity;
import com.mycompany.bookme.table.dao.models.entities.TableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customers", schema = "bookme")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerEntity extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String lastName;

  @Column(nullable = false)
  private String phone;

  @Column()
  private String email;

  @OneToMany(mappedBy = "customer")
  private List<ReservationEntity> reservationEntities;
}

