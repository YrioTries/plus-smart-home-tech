package ru.yandex.practicum.warehouse.model.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.model.entity.OrderBookingDao;

@UtilityClass
public class OrderBookingMapper {

    public static BookedProductsDto toDto(OrderBookingDao booking) {
        if (booking == null) return null;

        return BookedProductsDto.builder()
                .deliveryWeight(booking.getDeliveryWeight())
                .deliveryVolume(booking.getDeliveryVolume())
                .fragile(booking.getFragile())
                .build();
    }

    public static OrderBookingDao toEntity(BookedProductsDto dto) {
        if (dto == null) return null;

        return OrderBookingDao.builder()
                .deliveryWeight(dto.getDeliveryWeight() != null ? dto.getDeliveryWeight() : 0.0)
                .deliveryVolume(dto.getDeliveryVolume() != null ? dto.getDeliveryVolume() : 0.0)
                .fragile(dto.getFragile() != null ? dto.getFragile() : false)
                .build();
    }
}
