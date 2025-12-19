package ru.yandex.practicum.interaction_api.model.dto;

import lombok.Data;

@Data
public class BookedProductsDto {
    private Double deliveryWeight;
    private Double deliveryVolume;
    private Boolean fragile;
}
