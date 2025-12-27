package ru.yandex.practicum.interaction_api.model.dto;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class ShoppingCartDto {
    private UUID shoppingCartId;
    private Map<String, Integer> products;
}
