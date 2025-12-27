package ru.yandex.practicum.shopping_cart.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.shopping_cart.entity.CartProductEntity;
import ru.yandex.practicum.shopping_cart.entity.CartProductId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartProductRepository extends JpaRepository<CartProductEntity, CartProductId> {

    List<CartProductEntity> findByShoppingCart_Id(UUID cartId);

    Optional<CartProductEntity> findByShoppingCart_IdAndProductId(UUID shoppingCartId, UUID productId);

    void deleteByShoppingCart_IdAndProductId(UUID shoppingCartId, UUID productId);
}
