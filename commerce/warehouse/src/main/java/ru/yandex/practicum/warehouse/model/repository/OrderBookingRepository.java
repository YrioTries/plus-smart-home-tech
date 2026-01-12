package ru.yandex.practicum.warehouse.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.warehouse.model.entity.OrderBookingDao;

import java.util.UUID;

public interface OrderBookingRepository extends JpaRepository<OrderBookingDao, UUID> {
}
