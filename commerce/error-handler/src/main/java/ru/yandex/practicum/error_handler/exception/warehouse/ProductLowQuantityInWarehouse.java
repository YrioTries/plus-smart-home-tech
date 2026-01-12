package ru.yandex.practicum.error_handler.exception.warehouse;

public class ProductLowQuantityInWarehouse extends RuntimeException {
    public ProductLowQuantityInWarehouse(String message) {
        super(message);
    }
}
