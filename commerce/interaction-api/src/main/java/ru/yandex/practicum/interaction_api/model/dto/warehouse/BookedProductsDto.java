package ru.yandex.practicum.interaction_api.model.dto.warehouse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookedProductsDto {
    @Builder.Default
    private Double deliveryWeight = 0.0;
    @Builder.Default
    private Double deliveryVolume = 0.0;
    @Builder.Default
    private Boolean fragile = false;
}
