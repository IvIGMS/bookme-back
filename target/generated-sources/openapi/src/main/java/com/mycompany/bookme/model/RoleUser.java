package com.mycompany.bookme.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Role de los users de la app
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-10-30T10:31:37.615691+01:00[Europe/Madrid]")
public enum RoleUser {
  
  USER("USER"),
  
  ADMIN("ADMIN");

  private String value;

  RoleUser(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static RoleUser fromValue(String value) {
    for (RoleUser b : RoleUser.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

