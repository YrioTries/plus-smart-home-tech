package ru.yandex.practicum.interaction_api.model.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ChangeProductQuantityRequest {
    private UUID productId;
    private Integer newQuantity;
}
