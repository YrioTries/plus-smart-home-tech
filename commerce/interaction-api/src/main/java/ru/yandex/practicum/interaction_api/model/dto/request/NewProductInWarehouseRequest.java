package ru.yandex.practicum.interaction_api.model.dto.request;

import lombok.Getter;
import ru.yandex.practicum.interaction_api.model.dto.DimensionDto;

import java.util.UUID;

@Getter
public class NewProductInWarehouseRequest {
    private UUID productId;
    private boolean fragile;
    private DimensionDto dimension;
    private Double weight;
}
