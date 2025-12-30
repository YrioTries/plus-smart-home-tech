package ru.yandex.practicum.shopping_cart.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import ru.yandex.practicum.interaction_api.enums.ShoppingCartState;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "shopping_carts", schema = "public")
public class ShoppingCartEntity {
    @Id
    @UuidGenerator
    @Column(name = "shoppingCartId")
    private UUID shoppingCartId;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 10)
    private ShoppingCartState state;

    @Column(name = "owner", length = 20, nullable = false)
    private String owner;

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartProductEntity> cartProducts;
}
