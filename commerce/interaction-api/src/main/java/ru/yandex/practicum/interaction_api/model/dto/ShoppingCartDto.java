package ru.yandex.practicum.interaction_api.model.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ShoppingCartDto {
    private String shoppingCartId;
    private Map<String, Integer> products;
}
