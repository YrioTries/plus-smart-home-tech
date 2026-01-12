package ru.yandex.practicum.payment.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.payment.model.entity.Payment;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
