package ru.yandex.practicum.warehouse.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.warehouse.entity.WarehouseProductEntity;

import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<WarehouseProductEntity, UUID> {
    boolean existsByProductId(UUID productId);

    @Query("SELECT w.quantity FROM WarehouseProductEntity w WHERE w.productId = :productId")
    Integer findQuantityByProductId(@Param("productId") UUID productId);
}
