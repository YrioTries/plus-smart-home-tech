package ru.yandex.practicum.payment.service;

import ru.yandex.practicum.interaction_api.model.dto.order.OrderDto;
import ru.yandex.practicum.interaction_api.model.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {

    PaymentDto goToPayment(OrderDto order);

    BigDecimal calculateTotalCost(OrderDto order);

    void createRefund(UUID paymentId);

    BigDecimal calculateProductCost(OrderDto order);

    void failedPayment(UUID paymentId);
}
