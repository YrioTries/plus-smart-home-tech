package ru.yandex.practicum.interaction_api.model.dto.request;

import lombok.Getter;
import ru.yandex.practicum.interaction_api.enums.QuantityState;

import java.util.UUID;

@Getter
public class SetProductQuantityStateRequest {
    private UUID productId;
    private QuantityState quantityState;
}
