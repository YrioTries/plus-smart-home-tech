package ru.yandex.practicum.shopping_cart.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CartProductId implements Serializable {
    private String shoppingCart;  // cart_id
    private String productId;    // product_id
}
