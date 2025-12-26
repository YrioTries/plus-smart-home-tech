package ru.yandex.practicum.interaction_api.model.dto;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.interaction_api.enums.ProductCategory;
import ru.yandex.practicum.interaction_api.enums.ProductState;
import ru.yandex.practicum.interaction_api.enums.QuantityState;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDto {
    private String productId;
    private String productName;
    private String description;
    private String imageSrc;
    private QuantityState quantityState;
    private ProductState productState;
    private ProductCategory productCategory;
    private BigDecimal price;
}
