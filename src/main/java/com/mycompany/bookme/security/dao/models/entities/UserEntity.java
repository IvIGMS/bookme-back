package com.mycompany.bookme.security.dao.models.entities;

import com.mycompany.bookme.exceptions.utils.AuditableEntity;
import com.mycompany.bookme.security.dao.models.enums.RoleUserEnum;
import jakarta.persistence.*;
import lombok.*;

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
}

