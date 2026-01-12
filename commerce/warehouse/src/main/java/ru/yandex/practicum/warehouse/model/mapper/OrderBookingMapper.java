package ru.yandex.practicum.warehouse.model.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.warehouse.model.entity.OrderBooking;

@UtilityClass
public class OrderBookingMapper {

    public static BookedProductsDto toDto(OrderBooking booking) {
        if (booking == null) return null;

        return BookedProductsDto.builder()
                .deliveryWeight(booking.getDeliveryWeight())
                .deliveryVolume(booking.getDeliveryVolume())
                .fragile(booking.getFragile())
                .build();
    }

    public static OrderBooking toEntity(BookedProductsDto dto) {
        if (dto == null) return null;

        return OrderBooking.builder()
                .deliveryWeight(dto.getDeliveryWeight() != null ? dto.getDeliveryWeight() : 0.0)
                .deliveryVolume(dto.getDeliveryVolume() != null ? dto.getDeliveryVolume() : 0.0)
                .fragile(dto.getFragile() != null ? dto.getFragile() : false)
                .build();
    }
}
