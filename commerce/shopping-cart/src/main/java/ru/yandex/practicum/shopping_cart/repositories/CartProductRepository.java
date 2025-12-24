package ru.yandex.practicum.shopping_cart.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.shopping_cart.entity.CartProductEntity;
import ru.yandex.practicum.shopping_cart.entity.CartProductId;

import java.util.List;
import java.util.Optional;

public interface CartProductRepository extends JpaRepository<CartProductEntity, CartProductId> {

    List<CartProductEntity> findByShoppingCart_Id(String cartId);

    Optional<CartProductEntity> findByShoppingCart_IdAndProductId(String shoppingCartId, String productId);

    void deleteByShoppingCart_IdAndProductId(String shoppingCartId, String productId);
}
