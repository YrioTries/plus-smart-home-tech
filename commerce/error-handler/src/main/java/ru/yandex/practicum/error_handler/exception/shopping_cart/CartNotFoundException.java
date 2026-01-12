package ru.yandex.practicum.error_handler.exception.shopping_cart;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String message) {
        super(message);
    }
}
