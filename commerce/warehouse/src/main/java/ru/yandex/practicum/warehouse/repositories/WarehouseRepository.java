package ru.yandex.practicum.warehouse.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.warehouse.entity.WarehouseProductEntity;

public interface WarehouseRepository extends JpaRepository<WarehouseProductEntity, String> {
    boolean existsByProductId(String productId);

    @Query("SELECT w.quantity FROM WarehouseProductEntity w WHERE w.productId = :productId")
    Integer findQuantityByProductId(@Param("productId") String productId);
}
