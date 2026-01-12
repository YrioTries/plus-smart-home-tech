package ru.yandex.practicum.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction_api.model.dto.order.OrderDto;
import ru.yandex.practicum.interaction_api.model.dto.payment.PaymentDto;
import ru.yandex.practicum.payment.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDto goToPayment(@RequestBody @Valid OrderDto order) {
        return service.goToPayment(order);
    }

    @PostMapping("/totalCost")
    public BigDecimal calculateTotalCost(@RequestBody @Valid OrderDto order) {
        return service.calculateTotalCost(order);
    }

    @PostMapping("/refund")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createRefund(@RequestBody UUID paymentId) {
        service.createRefund(paymentId);
    }

    @PostMapping("/productCost")
    public BigDecimal calculateProductCost(@RequestBody @Valid OrderDto order) {
        return service.calculateProductCost(order);
    }

    @PostMapping("/failed")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void failedPayment(@RequestBody UUID paymentId) {
        service.failedPayment(paymentId);
    }
}
