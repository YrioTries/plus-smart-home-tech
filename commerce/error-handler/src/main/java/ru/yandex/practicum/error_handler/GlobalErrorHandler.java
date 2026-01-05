package ru.yandex.practicum.error_handler;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.error_handler.exception.*;

@Slf4j
@RestControllerAdvice
public class GlobalErrorHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFound(final ProductNotFoundException e) {
        log.error("Ресурс не найден: {}", e.getMessage());
        return new ErrorResponse("ERROR[404]: Произошла ошибка ProductNotFoundException: ", e.getMessage());
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

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse illegalArgument(final IllegalArgumentException e) {
        log.error("Некорректный аргумент: {}", e.getMessage());
        return new ErrorResponse("ERROR[400]: Некорректный параметр: ", e.getMessage());
    }

    @ExceptionHandler(InternalServerError.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(final InternalServerError e) {
        log.error("Внутренняя ошибка сервера: {}", e.getMessage(), e);
        return new ErrorResponse("ERROR[500]: Внутренняя ошибка сервера: ", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(final Exception e) {
        log.error("Внутренняя ошибка сервера: {}", e.getMessage(), e);
        return new ErrorResponse("ERROR[500]: Внутренняя ошибка сервера: ", e.getMessage());
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse httpMessageNotReadable(final NoSpecifiedProductInWarehouseException e) {
        log.error("Нет нужного товара на складе: {}", e.getMessage());
        return new ErrorResponse("ERROR[400]: Произошла ошибка NoSpecifiedProductInWarehouseException: ", e.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundFeign(final FeignException e) {
        log.error("Ресурс не найден: {}", e.getMessage());
        return new ErrorResponse("ERROR[404]: FeignException: ", e.getMessage());
    }

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFound(final NoProductsInShoppingCartException e) {
        log.error("Товар не найден в корзине: {}", e.getMessage());
        return new ErrorResponse("ERROR[404]: Произошла ошибка NotFoundException: ", e.getMessage());
    }

    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouse.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse httpMediaTypeNotSupported(final ProductInShoppingCartLowQuantityInWarehouse e) {
        log.error("Не достаточно товара на складе: {}", e.getMessage());
        return new ErrorResponse("ERROR[400]: Произошла ошибка ProductInShoppingCartLowQuantityInWarehouse: ", e.getMessage());
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse notFound(final NoSpecifiedProductInWarehouseException e) {
        log.error("Ресурс не найден: {}", e.getMessage());
        return new ErrorResponse("ERROR[400]: Произошла ошибка NoSpecifiedProductInWarehouseException: ", e.getMessage());
    }

    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse specifiedProductAlreadyInWarehouseException(final SpecifiedProductAlreadyInWarehouseException e) {
        log.error("Ошибка добавление уже существующего товара на склад: {}", e.getMessage());
        return new ErrorResponse("ERROR[409]: Произошла ошибка SpecifiedProductAlreadyInWarehouseException: ", e.getMessage());
    }
}
