package ru.yandex.practicum.error_handler.exception.shopping_cart;

public class DeactivatedCartException extends RuntimeException {
    public DeactivatedCartException(String message) {
        super(message);
    }
}
