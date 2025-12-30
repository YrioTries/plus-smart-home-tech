package ru.yandex.practicum.error_handler.exception;

public class DeactivatedCartException extends RuntimeException {
    public DeactivatedCartException(String message) {
        super(message);
    }
}
