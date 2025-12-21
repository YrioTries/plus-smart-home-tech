package ru.yandex.practicum.interaction_api.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CartProductId implements Serializable {
    private String shoppingCart;  // cart_id
    private String product;      // product_id
}
