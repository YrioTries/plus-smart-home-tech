package ru.yandex.practicum.interaction_api.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ShoppingCartDto {
    private String shoppingCartId;
    private List<Integer> products;
}
