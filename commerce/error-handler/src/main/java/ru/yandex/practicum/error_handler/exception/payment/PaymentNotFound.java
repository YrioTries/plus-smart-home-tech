package ru.yandex.practicum.error_handler.exception.payment;

public class PaymentNotFound extends RuntimeException {
    public PaymentNotFound(String message) {
        super(message);
    }
}
