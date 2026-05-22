package ru.yandex.practicum.payment.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.payment.model.entity.PaymentDao;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentDao, UUID> {
}
