package com.mycompany.bookme.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UserDTO
 */
@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-30T10:31:37.615691+01:00[Europe/Madrid]")
public class UserDTO {

  private Long id;

  private String email;

  private String firstname;

  private String lastname;

  private Boolean isEmailActive;

  private String role;

  private String tier;

}

