package ru.yandex.practicum.error_handler.exception;

public class InternalServerError extends RuntimeException {
  public InternalServerError(String message) {
    super(message);
  }
}
