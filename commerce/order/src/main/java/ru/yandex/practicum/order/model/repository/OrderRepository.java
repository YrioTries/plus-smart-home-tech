package ru.yandex.practicum.order.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.order.model.entity.OrderDao;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderDao, UUID> {

    Page<OrderDao> findByUsername(String username, Pageable pageable);

    Optional<OrderDao> findByPaymentId(UUID paymentId);

    Optional<OrderDao> findByDeliveryId(UUID deliveryId);
}
