package ru.yandex.practicum.interaction_api.model.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class OrderDto {

    @NotNull
    private UUID orderId;

    private UUID shoppingCartId;

    @NotNull
    private Map<@NotNull UUID, @NotNull @PositiveOrZero Integer> products;

    private UUID paymentId;

    private UUID deliveryId;

    @Builder.Default
    private OrderState state = OrderState.NEW;

    @Builder.Default
    private Double deliveryWeight = 0.0;

    @Builder.Default
    private Double deliveryVolume = 0.0;

    @Builder.Default
    private Boolean fragile = false;

    @Builder.Default
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal deliveryPrice = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal productPrice = BigDecimal.ZERO;
}
