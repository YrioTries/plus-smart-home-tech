package ru.yandex.practicum.delivery.service;

import ru.yandex.practicum.interaction_api.model.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction_api.model.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {

    DeliveryDto createDelivery(DeliveryDto delivery);

    void successfulDelivery(UUID deliveryId);

    void pickedDelivery(UUID deliveryId);

    void failedDelivery(UUID deliveryId);

    BigDecimal calculateDeliveryCost(OrderDto order);
}
