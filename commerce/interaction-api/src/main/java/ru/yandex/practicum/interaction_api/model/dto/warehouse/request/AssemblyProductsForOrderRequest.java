package ru.yandex.practicum.interaction_api.model.dto.warehouse.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class AssemblyProductsForOrderRequest {

    @NotNull
    private Map<@NotNull UUID, @NotNull @PositiveOrZero Integer> products;

    @NotNull
    private UUID orderId;
}
