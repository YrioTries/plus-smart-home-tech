package ru.yandex.practicum.error_handler.exception.warehouse;

public class ProductInWarehouseNotFoundException extends RuntimeException {
    public ProductInWarehouseNotFoundException(String message) {
        super(message);
    }
}
