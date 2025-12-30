package ru.yandex.practicum.interaction_api.model.dto;

import lombok.Data;

import java.util.Map;
import java.util.UUID;


public record ShoppingCartDto(UUID shoppingCartId, Map<UUID, Integer> products) {
}
