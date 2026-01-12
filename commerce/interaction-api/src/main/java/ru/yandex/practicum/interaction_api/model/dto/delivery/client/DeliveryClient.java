package ru.yandex.practicum.interaction_api.model.dto.delivery.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction_api.model.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction_api.model.dto.order.OrderDto;

import java.math.BigDecimal;

@FeignClient(name = "delivery")
public interface DeliveryClient {

    @PutMapping("/api/v1/delivery")
    DeliveryDto createDelivery(@RequestBody @Valid DeliveryDto delivery);

    @PostMapping("/api/v1/delivery/cost")
    BigDecimal calculateDeliveryCost(@RequestBody @Valid OrderDto order);
}
