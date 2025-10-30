package com.mycompany.bookme.security.dao.repositories;


import com.mycompany.bookme.model.UserDTO;
import com.mycompany.bookme.security.dao.models.entities.UserEntity;
import com.mycompany.bookme.security.dao.models.enums.RoleUserEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
  @Query(
      value =
          """
          SELECT
          u.id AS id,
          u.email AS email,
          u.firstname AS firstname,
          u.lastname AS lastname,
          u.is_active AS isEmailActive,
          u.role AS role,
          us.tier AS tier
          FROM users u
          INNER JOIN user_config uc ON uc.user_id = u.id
          INNER JOIN user_scope us ON uc.scope_id = us.id
          WHERE u.id = :userId
          """,
      nativeQuery = true)
  Optional<UserDTO> findByIdExtended(@Param("userId") Long userId);

  Optional<UserEntity> findByEmail(String email);

  @Query(
      value =
          " SELECT u.* from users u "
              + " INNER JOIN companies c ON c.id = u.company_id "
              + " WHERE c.owner_id = :ownerId",
      nativeQuery = true)
  List<UserEntity> getUsersByOwnerId(@Param("ownerId") Long ownerId);

  List<UserEntity> findByRole(RoleUserEnum role);
}
