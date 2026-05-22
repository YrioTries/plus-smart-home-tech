package ru.yandex.practicum.error_handler.exception.warehouse.booking;

public class NotOrderBookingFound extends RuntimeException {
    public NotOrderBookingFound(String message) {
        super(message);
    }
}
