package ru.yandex.practicum.interaction_api.model.dto.payment;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PaymentDto {

    private UUID paymentId;

    @PositiveOrZero
    private BigDecimal totalPayment;

    @PositiveOrZero
    private BigDecimal totalProduct;

    @PositiveOrZero
    private BigDecimal deliveryTotal;

    @PositiveOrZero
    private BigDecimal feeTotal;

    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;
}
