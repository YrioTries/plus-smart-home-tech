package ru.yandex.practicum.interaction_api.model.shopping_cart.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ChangeProductQuantityRequest {

    @NotNull
    private UUID productId;

    @NotNull
    private Integer newQuantity;
}
