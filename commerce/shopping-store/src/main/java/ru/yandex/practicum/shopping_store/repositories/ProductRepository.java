package ru.yandex.practicum.shopping_store.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.interaction_api.enums.ProductCategory;
import ru.yandex.practicum.interaction_api.enums.ProductState;
import ru.yandex.practicum.shopping_store.entity.ProductEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

    @Override
    Optional<ProductEntity> findById(UUID id);

    @Query("SELECT p FROM ProductEntity p " +
            "WHERE p.productCategory.getProductName() = :categoryName " +
            "AND p.productState = :state " +
            "ORDER BY p.id")
    Page<ProductEntity> findAllByProductCategoryNameAndProductState(
            @Param("categoryName") String categoryName,
            @Param("state") ProductState state,
            Pageable pageable);
}

