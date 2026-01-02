package ru.yandex.practicum.shopping_store.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.interaction_api.model.enums.ProductCategory;
import ru.yandex.practicum.shopping_store.entity.Product;

import java.util.UUID;

public interface ShoppingStoreRepository extends JpaRepository<Product, UUID> {

    Page<Product> findAllByProductCategory(ProductCategory category, Pageable pageable);
}