package ru.yandex.practicum.delivery.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.delivery.model.entity.DeliveryDao;

import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<DeliveryDao, UUID> {
}
