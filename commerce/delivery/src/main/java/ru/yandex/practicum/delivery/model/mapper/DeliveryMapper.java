package ru.yandex.practicum.delivery.model.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.delivery.model.entity.DeliveryDao;
import ru.yandex.practicum.delivery.model.entity.DeliveryAddress;
import ru.yandex.practicum.interaction_api.model.delivery.dto.DeliveryDto;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.AddressDto;

@UtilityClass
public class DeliveryMapper {

    public static DeliveryDto toDto(DeliveryDao entity) {
        if (entity == null) return null;

        return DeliveryDto.builder()
                .deliveryId(entity.getDeliveryId())
                .orderId(entity.getOrderId())
                .deliveryState(entity.getDeliveryState())
                .fromAddress(addressToDto(entity.getFromAddress()))
                .toAddress(addressToDto(entity.getToAddress()))
                .build();
    }

    public static DeliveryDao toEntity(DeliveryDto dto) {
        if (dto == null) return null;

        return DeliveryDao.builder()
                .deliveryId(dto.getDeliveryId())
                .orderId(dto.getOrderId())
                .deliveryState(dto.getDeliveryState())
                .fromAddress(dtoToAddress(dto.getFromAddress()))
                .toAddress(dtoToAddress(dto.getToAddress()))
                .build();
    }

    private static AddressDto addressToDto(DeliveryAddress address) {
        if (address == null) return null;

        return AddressDto.builder()
                .country(address.getCountry())
                .city(address.getCity())
                .street(address.getStreet())
                .house(address.getHouse())
                .flat(address.getFlat())
                .build();
    }

    private static DeliveryAddress dtoToAddress(AddressDto dto) {
        if (dto == null) return null;

        return DeliveryAddress.builder()
                .country(dto.getCountry())
                .city(dto.getCity())
                .street(dto.getStreet())
                .house(dto.getHouse())
                .flat(dto.getFlat())
                .build();
    }
}
