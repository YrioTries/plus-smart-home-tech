package ru.yandex.practicum.interaction_api.model.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class AddProductToWarehouseRequest {
    private UUID productId;
    private Integer quantity;
}
