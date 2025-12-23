package ru.yandex.practicum.shopping_cart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@IdClass(CartProductId.class)
@Table(name = "products_cart", schema = "public")
public class CartProductEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private ShoppingCartEntity shoppingCart;

    @Id
    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;
}

