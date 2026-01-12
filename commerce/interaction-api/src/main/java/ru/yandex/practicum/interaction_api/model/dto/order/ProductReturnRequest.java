package ru.yandex.practicum.interaction_api.model.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ProductReturnRequest {

    private UUID orderId;

    @NotNull
    private Map<@NotNull UUID, @NotNull @PositiveOrZero Integer> products;
}
