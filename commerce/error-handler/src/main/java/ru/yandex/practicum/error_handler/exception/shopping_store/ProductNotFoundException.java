package ru.yandex.practicum.error_handler.exception.shopping_store;

public class ProductNotFoundException extends RuntimeException {
  public ProductNotFoundException(String message) {
    super(message);
  }
}
