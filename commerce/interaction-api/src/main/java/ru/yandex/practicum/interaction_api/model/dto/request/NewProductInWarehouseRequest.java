package ru.yandex.practicum.interaction_api.model.dto.request;

import lombok.Getter;
import ru.yandex.practicum.interaction_api.model.dto.DimensionDto;

@Getter
public class NewProductInWarehouseRequest {
    private String productId;
    private boolean fragile;
    private DimensionDto dimension;
    private Double weight;
}
