package ru.yandex.practicum.warehouse.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.warehouse.entity.ProductInWarehouseDao;

import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<ProductInWarehouseDao, UUID> {
}
