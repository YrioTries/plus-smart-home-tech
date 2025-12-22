package ru.yandex.practicum.shopping_cart.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.interaction_api.model.entity.helpers.CartProductId;
import ru.yandex.practicum.interaction_api.model.entity.CartProductEntity;

import java.util.List;

public interface CartProductRepository extends JpaRepository<CartProductEntity, CartProductId> {
    @Query("SELECT cp FROM CartProductEntity cp WHERE cp.shoppingCart.id = :cartId")
    List<CartProductEntity> findByCartId(@Param("cartId") String cartId); // нахождение корзины по id корзины

    @Query("SELECT COALESCE(SUM(c.quantity), 0) FROM CartProductEntity c WHERE c.shoppingCart.id = :cartId")
    Integer getTotalItemsCount(@Param("cartId") String cartId); // нахождение количества товара в корзине
}
