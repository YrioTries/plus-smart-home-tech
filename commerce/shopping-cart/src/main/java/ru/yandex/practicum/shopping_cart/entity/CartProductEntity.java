package ru.yandex.practicum.shopping_cart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@IdClass(CartProductId.class)
@Table(name = "products_cart", schema = "public")
public class CartProductEntity {
    @Id
    @Column(name = "cart_id", nullable = false)
    private UUID cartId;

    @Id
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", insertable = false, updatable = false)
    private ShoppingCartEntity shoppingCart;
}

