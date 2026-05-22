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
import ru.yandex.practicum.error_handler.exception.delivery.NoDeliveryFoundException;
import ru.yandex.practicum.error_handler.exception.order.NoOrderFoundException;
import ru.yandex.practicum.error_handler.exception.payment.PaymentNotFound;
import ru.yandex.practicum.error_handler.exception.shopping_cart.NoProductsInShoppingCartException;
import ru.yandex.practicum.error_handler.exception.shopping_store.ProductNotFoundException;
import ru.yandex.practicum.error_handler.exception.warehouse.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.error_handler.exception.warehouse.ProductLowQuantityInWarehouse;
import ru.yandex.practicum.error_handler.exception.warehouse.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.error_handler.exception.warehouse.booking.NotOrderBookingFound;
import ru.yandex.practicum.error_handler.exception.warehouse.ProductInShoppingCartLowQuantityInWarehouse;

@Slf4j
@RestControllerAdvice
public class GlobalErrorHandler {

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

    @ExceptionHandler(ProductLowQuantityInWarehouse.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse httpMessageNotReadable(final ProductLowQuantityInWarehouse e) {
        log.error("Недостаточно товара на складе: {}", e.getMessage());
        return new ErrorResponse("ERROR[400]: Произошла ошибка ProductLowQuantityInWarehouse: ", e.getMessage());
    }

    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouse.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse httpMediaTypeNotSupported(final ProductInShoppingCartLowQuantityInWarehouse e) {
        log.error("Не достаточно товара на складе: {}", e.getMessage());
        return new ErrorResponse("ERROR[400]: Произошла ошибка ProductInShoppingCartLowQuantityInWarehouse: ", e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse illegalArgument(final IllegalArgumentException e) {
        log.error("Некорректный аргумент: {}", e.getMessage());
        return new ErrorResponse("ERROR[400]: Некорректный параметр: ", e.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundFeign(final FeignException e) {
        log.error("Ресурс Feign не найден: {}", e.getMessage());
        return new ErrorResponse("ERROR[404]: FeignException: ", e.getMessage());
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundProductInWarehouse(final NoSpecifiedProductInWarehouseException e) {
        log.error("Нет нужного товара на складе: {}", e.getMessage());
        return new ErrorResponse("ERROR[404]: Произошла ошибка NoSpecifiedProductInWarehouseException: ", e.getMessage());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundProduct(final ProductNotFoundException e) {
        log.error("Продукт не найден: {}", e.getMessage());
        return new ErrorResponse("ERROR[404]: Произошла ошибка ProductNotFoundException: ", e.getMessage());
    }

    @ExceptionHandler(NoDeliveryFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundDelivery(final NoDeliveryFoundException e) {
        log.error("Доставка не найдена: {}", e.getMessage());
        return new ErrorResponse("ERROR[404]: NoDeliveryFoundException: ", e.getMessage());
    }

    @ExceptionHandler(PaymentNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundPayment(final PaymentNotFound e) {
        log.error("Оплата не найдена: {}", e.getMessage());
        return new ErrorResponse("ERROR[404]: PaymentNotFound: ", e.getMessage());
    }

    @ExceptionHandler(NoOrderFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundOrder(final NoOrderFoundException e) {
        log.error("Заказ не найден: {}", e.getMessage());
        return new ErrorResponse("ERROR[404]: NoOrderFoundException: ", e.getMessage());
    }

    @ExceptionHandler(NotOrderBookingFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundOrderBooking(final NotOrderBookingFound e) {
        log.error("Зарезервированный заказ не найден: {}", e.getMessage());
        return new ErrorResponse("ERROR[404]: NotOrderBookingFound: ", e.getMessage());
    }

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundProductsInShoppingCart(final NoProductsInShoppingCartException e) {
        log.error("Товар не найден в корзине: {}", e.getMessage());
        return new ErrorResponse("ERROR[404]: Произошла ошибка NotFoundException: ", e.getMessage());
    }

    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse specifiedProductAlreadyInWarehouseException(final SpecifiedProductAlreadyInWarehouseException e) {
        log.error("Ошибка добавления уже существующего товара на склад: {}", e.getMessage());
        return new ErrorResponse("ERROR[409]: Произошла ошибка SpecifiedProductAlreadyInWarehouseException: ", e.getMessage());
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
}
