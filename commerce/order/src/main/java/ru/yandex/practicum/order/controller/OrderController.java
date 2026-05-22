package ru.yandex.practicum.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction_api.model.order.dto.request.CreateNewOrderRequest;
import ru.yandex.practicum.interaction_api.model.order.dto.OrderDto;
import ru.yandex.practicum.interaction_api.model.order.dto.request.ProductReturnRequest;
import ru.yandex.practicum.order.service.OrderService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @GetMapping
    public Page<OrderDto> getOrder(@RequestParam String username, @PageableDefault Pageable pageable) {
        return service.getOrder(username, pageable);
    }

    @PostMapping("/get/payment")
    public OrderDto getOrderByPayment(@RequestBody UUID paymentId) {
        return service.getOrderByPayment(paymentId);
    }

    @PostMapping("/get/delivery")
    public OrderDto getOrderByDelivery(@RequestBody UUID deliveryId) {
        return service.getOrderByDelivery(deliveryId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping
    public OrderDto createOrder(@RequestParam String username, @RequestBody @Valid CreateNewOrderRequest request) {
        return service.createOrder(username, request);
    }

    @PostMapping("/return")
    public OrderDto returnOrder(@RequestBody @Valid ProductReturnRequest request) {
        return service.returnOrder(request);
    }

    @PostMapping("/payment")
    public OrderDto paymentOrder(@RequestBody UUID orderId) {
        return service.paymentOrder(orderId);
    }

    @PostMapping("/payment/failed")
    public OrderDto failedPaymentOrder(@RequestBody UUID orderId) {
        return service.failedPaymentOrder(orderId);
    }

    @PostMapping("/delivery")
    public OrderDto deliveryOrder(@RequestBody UUID orderId) {
        return service.deliveryOrder(orderId);
    }

    @PostMapping("/delivery/failed")
    public OrderDto failedDeliveryOrder(@RequestBody UUID orderId) {
        return service.failedDeliveryOrder(orderId);
    }

    @PostMapping("/completed")
    public OrderDto completedOrder(@RequestBody UUID orderId) {
        return service.completedOrder(orderId);
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateTotalOrder(@RequestBody UUID orderId) {
        return service.calculateTotalOrder(orderId);
    }

    @PostMapping("/calculate/delivery")
    public OrderDto calculateDeliveryOrder(@RequestBody UUID orderId) {
        return service.calculateDeliveryOrder(orderId);
    }

    @PostMapping("/assembly")
    public OrderDto assemblyOrder(@RequestBody UUID orderId) {
        return service.assemblyOrder(orderId);
    }

    @PostMapping("/assembly/failed")
    public OrderDto failedAssemblyOrder(@RequestBody UUID orderId) {
        return service.failedAssemblyOrder(orderId);
    }
}
