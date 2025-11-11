package com.mycompany.bookme.reservation.dao.models.entities;

import com.mycompany.bookme.customer.dao.models.entities.CustomerEntity;
import com.mycompany.bookme.exceptions.utils.AuditableEntity;
import com.mycompany.bookme.table.dao.models.entities.TableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "reservations", schema = "bookme")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReservationEntity extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private OffsetDateTime dateTime;

  @Column(nullable = false)
  private Integer numberOfPeople;

  @Column(nullable = false)
  private String status;

  @ManyToOne
  @JoinColumn(name = "table_id")
  private TableEntity table;

  @ManyToOne
  @JoinColumn(name = "customer_id")
  private CustomerEntity customer;
}

