package ru.yandex.practicum.shopping_cart.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.shopping_cart.entity.ShoppingCartDao;

import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCartDao, UUID> {

    Optional<ShoppingCartDao> findByOwner(String owner);

}
