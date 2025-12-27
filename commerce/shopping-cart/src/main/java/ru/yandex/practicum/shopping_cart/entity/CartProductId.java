package ru.yandex.practicum.shopping_cart.entity;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CartProductId implements Serializable {
    private UUID shoppingCart;  // cart_id
    private UUID productId;    // product_id
}
