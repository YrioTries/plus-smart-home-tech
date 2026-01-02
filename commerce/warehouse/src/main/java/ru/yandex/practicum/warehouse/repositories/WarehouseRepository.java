package ru.yandex.practicum.warehouse.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.warehouse.entity.ProductInWarehouse;

import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<ProductInWarehouse, UUID> {
}
