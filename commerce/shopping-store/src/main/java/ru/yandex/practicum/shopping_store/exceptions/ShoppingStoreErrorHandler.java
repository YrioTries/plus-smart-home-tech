package ru.yandex.practicum.shopping_store.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.interaction_api.exception.ErrorResponse;
import ru.yandex.practicum.interaction_api.exception.ProductNotFoundException;

@Slf4j
@RestControllerAdvice
public class ShoppingStoreErrorHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFound(final ProductNotFoundException e) {
        log.error("Ресурс не найден: {}", e.getMessage());
        return new ErrorResponse("ERROR[404]: Произошла ошибка NotFoundException: ", e.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse httpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException e) {
        log.error("Неподдерживаемый медиа-тип: {}", e.getMessage());
        return new ErrorResponse("ERROR[400]: Произошла ошибка HttpMediaTypeNotSupportedException: ", e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse httpMessageNotReadable(final HttpMessageNotReadableException e) {
        log.error("Ошибка чтения HTTP сообщения: {}", e.getMessage());
        return new ErrorResponse("ERROR[400]: Произошла ошибка HttpMessageNotReadableException: ", e.getMessage());
    }
}
