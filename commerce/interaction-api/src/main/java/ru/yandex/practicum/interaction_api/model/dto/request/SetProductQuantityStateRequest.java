package ru.yandex.practicum.interaction_api.model.dto.request;

import lombok.Getter;

@Getter
public class SetProductQuantityStateRequest {
    private String productId;
    private String quantityState;
}
