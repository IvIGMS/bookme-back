package com.mycompany.bookme.exceptions;

public class ConflictException extends RuntimeException {
  public ConflictException(String message) {
    super(message);
  }

  public ConflictException(String message, Object... args) {
    super(String.format(message, args));
  }
}
