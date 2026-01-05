package ru.yandex.practicum.error_handler.exception;

public class ProductInWarehouseNotFoundException extends RuntimeException {
    public ProductInWarehouseNotFoundException(String message) {
        super(message);
    }
}
