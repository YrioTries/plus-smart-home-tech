package ru.yandex.practicum.delivery.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.model.entity.DeliveryDao;
import ru.yandex.practicum.delivery.model.repository.DeliveryRepository;
import ru.yandex.practicum.interaction_api.model.delivery.dto.DeliveryDto;
import ru.yandex.practicum.interaction_api.model.delivery.dto.DeliveryState;
import ru.yandex.practicum.interaction_api.model.order.client.OrderClient;
import ru.yandex.practicum.interaction_api.model.order.dto.OrderDto;
import ru.yandex.practicum.interaction_api.model.warehouse.client.WarehouseClient;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.AddressDto;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.request.ShippedToDeliveryRequest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceImplTest {

    @Mock
    private DeliveryRepository repository;

    @Mock
    private OrderClient orderClient;

    @Mock
    private WarehouseClient warehouseClient;

    @InjectMocks
    private DeliveryServiceImpl service;

    @Test
    void createDelivery_shouldSaveAndReturnDto() {
        DeliveryDto dto = DeliveryDto.builder()
                .fromAddress(AddressDto.builder().country("RU").city("Samara").street("Lenina").house("1").flat("1").build())
                .toAddress(AddressDto.builder().country("RU").city("Moscow").street("Tverskaya").house("2").flat("10").build())
                .orderId(UUID.randomUUID())
                .build();

        DeliveryDao saved = DeliveryDao.builder()
                .deliveryId(UUID.randomUUID())
                .orderId(dto.getOrderId())
                .deliveryState(DeliveryState.CREATED)
                .build();

        when(repository.save(any(DeliveryDao.class))).thenReturn(saved);

        DeliveryDto result = service.createDelivery(dto);

        assertNotNull(result);
        verify(repository).save(any(DeliveryDao.class));
    }

    @Test
    void successfulDelivery_shouldSetDeliveredAndNotifyOrder() {
        UUID deliveryId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        DeliveryDao delivery = DeliveryDao.builder()
                .deliveryId(deliveryId)
                .deliveryState(DeliveryState.IN_PROGRESS)
                .build();

        OrderDto order = OrderDto.builder()
                .orderId(orderId)
                .build();

        when(repository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(orderClient.getOrderByDelivery(deliveryId)).thenReturn(order);

        service.successfulDelivery(deliveryId);

        assertEquals(DeliveryState.DELIVERED, delivery.getDeliveryState());
        verify(repository).save(delivery);
        verify(orderClient).deliveryOrder(orderId);
    }

    @Test
    void pickedDelivery_shouldSetInProgressAndNotifyWarehouse() {
        UUID deliveryId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        DeliveryDao delivery = DeliveryDao.builder()
                .deliveryId(deliveryId)
                .deliveryState(DeliveryState.CREATED)
                .build();

        OrderDto order = OrderDto.builder()
                .orderId(orderId)
                .build();

        when(repository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(orderClient.getOrderByDelivery(deliveryId)).thenReturn(order);

        service.pickedDelivery(deliveryId);

        assertEquals(DeliveryState.IN_PROGRESS, delivery.getDeliveryState());
        verify(repository).save(delivery);
        verify(orderClient).assemblyOrder(orderId);
        verify(warehouseClient).shippedOrder(any());
    }

}
