package ru.yandex.practicum.shopping_cart.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.shopping_cart.entity.CartProductEntity;
import ru.yandex.practicum.shopping_cart.entity.CartProductId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartProductRepository extends JpaRepository<CartProductEntity, CartProductId> {
    Optional<CartProductEntity> findByCartIdAndProductId(UUID cartId, UUID productId);

    List<CartProductEntity> findByCartId(UUID cartId);

    void deleteByCartIdAndProductId(UUID cartId, UUID productId);

    List<CartProductEntity> findByCartIdAndProductIdsIn(UUID cartId, List<UUID> productIds);
}
