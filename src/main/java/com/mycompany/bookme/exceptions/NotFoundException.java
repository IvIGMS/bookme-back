package com.mycompany.bookme.exceptions;

public class NotFoundException extends RuntimeException {
  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException(String message, Object... args) {
    super(String.format(message, args));
  }
}
