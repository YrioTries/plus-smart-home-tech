package ru.yandex.practicum.interaction_api.model.dto.request;

import lombok.Getter;

@Getter
public class AddProductToWarehouseRequest {
    private String productId;
    private Integer quantity;
}
