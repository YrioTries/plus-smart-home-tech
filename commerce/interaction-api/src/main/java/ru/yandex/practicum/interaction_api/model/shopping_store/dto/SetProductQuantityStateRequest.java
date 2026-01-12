package ru.yandex.practicum.interaction_api.model.shopping_store.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.QuantityState;

import java.util.UUID;

@Data
@Builder
public class SetProductQuantityStateRequest {

    @NotNull
    private UUID productId;

    @NotNull
    private QuantityState quantityState;
}
