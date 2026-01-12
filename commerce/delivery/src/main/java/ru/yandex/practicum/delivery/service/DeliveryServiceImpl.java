package ru.yandex.practicum.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.delivery.model.Delivery;
import ru.yandex.practicum.delivery.model.DeliveryAddress;
import ru.yandex.practicum.delivery.model.mapper.DeliveryMapper;
import ru.yandex.practicum.delivery.model.repository.DeliveryRepository;
import ru.yandex.practicum.error_handler.exception.delivery.NoDeliveryFoundException;
import ru.yandex.practicum.interaction_api.clients.WarehouseClient;
import ru.yandex.practicum.interaction_api.model.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction_api.model.dto.delivery.DeliveryState;
import ru.yandex.practicum.interaction_api.model.dto.order.OrderDto;
import ru.yandex.practicum.interaction_api.model.dto.order.client.OrderClient;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.request.ShippedToDeliveryRequest;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository repository;

    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    private final BigDecimal BASE_DELIVERY_COST = BigDecimal.valueOf(5.0);
    private final BigDecimal FRAGILE_RATIO = BigDecimal.valueOf(0.2);
    private final BigDecimal WEIGHT_RATIO = BigDecimal.valueOf(0.3);
    private final BigDecimal VOLUME_RATIO = BigDecimal.valueOf(0.2);
    private final BigDecimal ADDRESS_RATIO = BigDecimal.valueOf(0.2);

    @Override
    public DeliveryDto createDelivery(DeliveryDto delivery) {
        return DeliveryMapper.toDto(repository.save(DeliveryMapper.toEntity(delivery)));
    }

    @Override
    public void successfulDelivery(UUID deliveryId) {
        Delivery delivery = getDelivery(deliveryId);

        delivery.setDeliveryState(DeliveryState.DELIVERED);
        repository.save(delivery);

        OrderDto order = orderClient.getOrderByDelivery(deliveryId);
        orderClient.deliveryOrder(order.getOrderId());

        log.info("Заказ с id: {} успешно доставлен!", order.getOrderId());
    }

    @Override
    public void pickedDelivery(UUID deliveryId) {
        Delivery delivery = getDelivery(deliveryId);

        OrderDto order = orderClient.getOrderByDelivery(deliveryId);
        orderClient.assemblyOrder(order.getOrderId());

        warehouseClient.shippedOrder(ShippedToDeliveryRequest.builder()
                .deliveryId(deliveryId)
                .orderId(order.getOrderId())
                .build());

        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        repository.save(delivery);

        log.info("Товар для доставки с id: {} успешно получен!", deliveryId);
    }

    @Override
    public void failedDelivery(UUID deliveryId) {
        Delivery delivery = getDelivery(deliveryId);

        delivery.setDeliveryState(DeliveryState.CANCELLED);
        repository.save(delivery);

        OrderDto order = orderClient.getOrderByDelivery(deliveryId);
        orderClient.failedDeliveryOrder(order.getOrderId());

        log.warn("Неудачно вручен товар из доставки с id: {} !", deliveryId);
    }

    @Override
    public BigDecimal calculateDeliveryCost(OrderDto order) {
        Delivery delivery = getDelivery(order.getDeliveryId());
        return calculateDelivery(delivery, order);
    }

    private Delivery getDelivery(UUID deliveryId) {
        return repository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставка с id: " + deliveryId + " не найдена!"));
    }

    private boolean isAddressContains(DeliveryAddress address, String substring) {
        if (address == null || substring == null) return false;

        return (address.getCountry().contains(substring)
                || address.getCity().contains(substring)
                || address.getStreet().contains(substring)
                || address.getHouse().contains(substring)
                || address.getFlat().contains(substring));
    }

    private BigDecimal calculateDelivery(Delivery delivery, OrderDto order) {
        BigDecimal deliveryPrice = BASE_DELIVERY_COST;

        if (isAddressContains(delivery.getFromAddress(), "ADDRESS_1")) {
            deliveryPrice = deliveryPrice.multiply(BigDecimal.valueOf(1)).add(BASE_DELIVERY_COST);
        }
        else if (isAddressContains(delivery.getFromAddress(), "ADDRESS_2")) {
            deliveryPrice = deliveryPrice.multiply(BigDecimal.valueOf(2)).add(BASE_DELIVERY_COST);
        }

        if (order.getFragile()) {
            deliveryPrice = deliveryPrice.add(deliveryPrice.multiply(FRAGILE_RATIO));
        }

        deliveryPrice = deliveryPrice.add(BigDecimal.valueOf(order.getDeliveryWeight()).multiply(WEIGHT_RATIO));
        deliveryPrice = deliveryPrice.add(BigDecimal.valueOf(order.getDeliveryVolume()).multiply(VOLUME_RATIO));

        if (!delivery.getFromAddress().getStreet().equals(delivery.getToAddress().getStreet())) {
            deliveryPrice = deliveryPrice.add(deliveryPrice.multiply(ADDRESS_RATIO));
        }

        return deliveryPrice;
    }
}
