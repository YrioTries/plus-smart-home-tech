package ru.yandex.practicum.payment.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ru.yandex.practicum.interaction_api.model.payment.dto.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDao {

    @Id
    @UuidGenerator
    @Column(name = "payment_id")
    private UUID paymentId;

    @NotNull
    @Column(name = "total_payment", nullable = false)
    private BigDecimal totalPayment;

    @Column(name = "total_product", nullable = false)
    private BigDecimal totalProduct;

    @Column(name = "delivery_total", nullable = false)
    private BigDecimal deliveryTotal;

    @Column(name = "fee_total", nullable = false)
    private BigDecimal feeTotal;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;
}
