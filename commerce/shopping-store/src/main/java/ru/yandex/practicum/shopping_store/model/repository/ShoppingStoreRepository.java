package ru.yandex.practicum.shopping_store.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.interaction_api.model.shopping_store.dto.ProductCategory;
import ru.yandex.practicum.shopping_store.model.entity.ProductDao;

import java.util.UUID;

public interface ShoppingStoreRepository extends JpaRepository<ProductDao, UUID> {

    Page<ProductDao> findAllByProductCategory(ProductCategory category, Pageable pageable);
}