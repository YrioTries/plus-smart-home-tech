package ru.yandex.practicum.shopping_cart.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.interaction_api.model.entity.ShoppingCartEntity;

import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCartEntity, String> {

    Optional<ShoppingCartEntity> findByOwner(String owner);
}
