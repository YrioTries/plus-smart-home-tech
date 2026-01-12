package ru.yandex.practicum.interaction_api.model.warehouse.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.DimensionDto;

import java.util.UUID;

@Data
@Builder
public class NewProductInWarehouseRequest {

    @NotNull
    private UUID productId;

    @NotNull
    private Boolean fragile;

    @NotNull
    private DimensionDto dimension;

    @NotNull
    @Min(1)
    private double weight;
}
