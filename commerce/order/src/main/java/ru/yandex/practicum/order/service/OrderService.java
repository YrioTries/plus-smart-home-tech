package ru.yandex.practicum.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.interaction_api.model.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.interaction_api.model.dto.order.OrderDto;
import ru.yandex.practicum.interaction_api.model.dto.order.ProductReturnRequest;

import java.util.UUID;

public interface OrderService {

    Page<OrderDto> getOrder(String username, Pageable pageable);

    OrderDto getOrderByPayment(UUID paymentId);

    OrderDto getOrderByDelivery(UUID deliveryId);

    OrderDto createOrder(String username, CreateNewOrderRequest request);

    OrderDto returnOrder(ProductReturnRequest request);

    OrderDto paymentOrder(UUID orderId);

    OrderDto failedPaymentOrder(UUID orderId);

    OrderDto deliveryOrder(UUID orderId);

    OrderDto failedDeliveryOrder(UUID orderId);

    OrderDto completedOrder(UUID orderId);

    OrderDto calculateTotalOrder(UUID orderId);

    OrderDto calculateDeliveryOrder(UUID orderId);

    OrderDto assemblyOrder(UUID orderId);

    OrderDto failedAssemblyOrder(UUID orderId);
}
