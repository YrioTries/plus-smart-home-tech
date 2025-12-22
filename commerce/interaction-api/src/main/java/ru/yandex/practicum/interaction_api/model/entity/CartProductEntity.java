package ru.yandex.practicum.interaction_api.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.interaction_api.model.entity.helpers.CartProductId;

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
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;
}
