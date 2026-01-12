package ru.yandex.practicum.interaction_api.model.dto.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction_api.model.dto.order.OrderDto;

import java.util.UUID;

@FeignClient(name = "order")
public interface OrderClient {

    String BASE_URL = "/api/v1/order";

    @PostMapping(BASE_URL+"/get/payment")
    OrderDto getOrderByPayment(@RequestBody UUID paymentId);

    @PostMapping(BASE_URL+"/get/delivery")
    OrderDto getOrderByDelivery(@RequestBody UUID deliveryId);

    @PostMapping(BASE_URL+"/payment")
    OrderDto paymentOrder(@RequestBody UUID orderId);

    @PostMapping(BASE_URL+"/payment/failed")
    OrderDto failedPaymentOrder(@RequestBody UUID orderId);

    @PostMapping(BASE_URL+"/delivery")
    OrderDto deliveryOrder(@RequestBody UUID orderId);

    @PostMapping(BASE_URL+"/delivery/failed")
    OrderDto failedDeliveryOrder(@RequestBody UUID orderId);

    @PostMapping(BASE_URL+"/assembly")
    OrderDto assemblyOrder(@RequestBody UUID orderId);
}
