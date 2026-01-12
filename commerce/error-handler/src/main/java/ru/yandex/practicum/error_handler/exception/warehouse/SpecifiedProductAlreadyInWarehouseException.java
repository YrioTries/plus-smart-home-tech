package ru.yandex.practicum.error_handler.exception.warehouse;

public class SpecifiedProductAlreadyInWarehouseException extends RuntimeException {
    public SpecifiedProductAlreadyInWarehouseException(String message) {
        super(message);
    }
}
