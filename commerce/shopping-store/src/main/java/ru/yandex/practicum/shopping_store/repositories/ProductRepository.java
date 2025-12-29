package ru.yandex.practicum.shopping_store.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.interaction_api.enums.ProductCategory;
import ru.yandex.practicum.interaction_api.enums.ProductState;
import ru.yandex.practicum.interaction_api.model.dto.Pageable;
import ru.yandex.practicum.shopping_store.entity.ProductEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

    @Override
    Optional<ProductEntity> findById(UUID id);

    Page<ProductEntity> findAllByProductCategoryAndProductStateOrderById(
            ProductCategory category,
            ProductState state,
            Pageable pageable);
}