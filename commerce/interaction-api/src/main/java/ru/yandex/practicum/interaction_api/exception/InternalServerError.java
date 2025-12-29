package ru.yandex.practicum.interaction_api.exception;

public class InternalServerError extends RuntimeException {
  public InternalServerError(String message) {
    super(message);
  }
}
