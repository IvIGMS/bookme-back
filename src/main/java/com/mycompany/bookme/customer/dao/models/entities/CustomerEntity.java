package com.mycompany.bookme.customer.dao.models.entities;

import java.util.List;

import com.mycompany.bookme.exceptions.utils.AuditableEntity;
import com.mycompany.bookme.reservation.dao.models.entities.ReservationEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

