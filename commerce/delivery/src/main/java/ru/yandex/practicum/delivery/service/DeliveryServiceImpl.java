package ru.yandex.practicum.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.model.entity.DeliveryDao;
import ru.yandex.practicum.delivery.model.entity.DeliveryAddress;
import ru.yandex.practicum.delivery.model.mapper.DeliveryMapper;
import ru.yandex.practicum.delivery.model.repository.DeliveryRepository;
import ru.yandex.practicum.error_handler.exception.delivery.NoDeliveryFoundException;
import ru.yandex.practicum.interaction_api.model.warehouse.client.WarehouseClient;
import ru.yandex.practicum.interaction_api.model.delivery.dto.DeliveryDto;
import ru.yandex.practicum.interaction_api.model.delivery.dto.DeliveryState;
import ru.yandex.practicum.interaction_api.model.order.dto.OrderDto;
import ru.yandex.practicum.interaction_api.model.order.client.OrderClient;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.request.ShippedToDeliveryRequest;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
    @Transactional
    public DeliveryDto createDelivery(DeliveryDto delivery) {
        return DeliveryMapper.toDto(repository.save(DeliveryMapper.toEntity(delivery)));
    }

    @Override
    @Transactional
    public void successfulDelivery(UUID deliveryId) {
        DeliveryDao delivery = getDelivery(deliveryId);

        delivery.setDeliveryState(DeliveryState.DELIVERED);
        repository.save(delivery);

        OrderDto order = orderClient.getOrderByDelivery(deliveryId);
        orderClient.deliveryOrder(order.getOrderId());

        log.info("Заказ с id: {} успешно доставлен!", order.getOrderId());
    }

    @Override
    @Transactional
    public void pickedDelivery(UUID deliveryId) {
        DeliveryDao delivery = getDelivery(deliveryId);

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
        DeliveryDao delivery = getDelivery(deliveryId);

        delivery.setDeliveryState(DeliveryState.CANCELLED);
        repository.save(delivery);

        OrderDto order = orderClient.getOrderByDelivery(deliveryId);
        orderClient.failedDeliveryOrder(order.getOrderId());

        log.warn("Неудачно вручен товар из доставки с id: {} !", deliveryId);
    }

    @Override
    public BigDecimal calculateDeliveryCost(OrderDto order) {
        DeliveryDao delivery = getDelivery(order.getDeliveryId());
        return calculateDelivery(delivery, order);
    }

    private DeliveryDao getDelivery(UUID deliveryId) {
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

    private BigDecimal calculateDelivery(DeliveryDao delivery, OrderDto order) {
        log.debug("Начало расчёта стоимости доставки для заказа: {}", order.getOrderId());
        BigDecimal deliveryPrice = BASE_DELIVERY_COST;
        log.debug("Базовая стоимость доставки: {}", deliveryPrice);

        if (isAddressContains(delivery.getFromAddress(), "ADDRESS_1")) {
            deliveryPrice = deliveryPrice.multiply(BigDecimal.ONE).add(BASE_DELIVERY_COST);
            log.debug("Стоимость увеличена из-за ADDRESS_1: {}", deliveryPrice);
        } else if (isAddressContains(delivery.getFromAddress(), "ADDRESS_2")) {
            deliveryPrice = deliveryPrice.multiply(BigDecimal.valueOf(2)).add(BASE_DELIVERY_COST);
            log.debug("Стоимость увеличена из-за ADDRESS_2: {}", deliveryPrice);
        }

        if (order.getFragile()) {
            BigDecimal fragileCost = deliveryPrice.multiply(FRAGILE_RATIO);
            deliveryPrice = deliveryPrice.add(fragileCost);
            log.debug("Стоимость увеличена из-за хрупкости: {}", deliveryPrice);
        }

        BigDecimal weightCost = BigDecimal.valueOf(order.getDeliveryWeight()).multiply(WEIGHT_RATIO);
        BigDecimal volumeCost = BigDecimal.valueOf(order.getDeliveryVolume()).multiply(VOLUME_RATIO);
        deliveryPrice = deliveryPrice.add(weightCost).add(volumeCost);
        log.debug("Стоимость с учётом веса и объёма: {}", deliveryPrice);


        if (!delivery.getFromAddress().getStreet().equals(delivery.getToAddress().getStreet())) {
            BigDecimal addressCost = deliveryPrice.multiply(ADDRESS_RATIO);
            deliveryPrice = deliveryPrice.add(addressCost);
            log.debug("Стоимость увеличена из-за разных улиц: {}", deliveryPrice);
        }

        log.info("Итоговая стоимость доставки для заказа {}: {}", order.getOrderId(), deliveryPrice);
        return deliveryPrice;
    }
}
