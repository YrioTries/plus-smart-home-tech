package ru.yandex.practicum.interaction_api.model.dto;

import lombok.Data;
import ru.yandex.practicum.interaction_api.enums.ProductCategory;
import ru.yandex.practicum.interaction_api.enums.ProductState;
import ru.yandex.practicum.interaction_api.enums.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;


public record ProductDto(UUID productId, String productName, String description, String imageSrc,
                         QuantityState quantityState, ProductState productState, ProductCategory productCategory,
                         BigDecimal price) {
}
