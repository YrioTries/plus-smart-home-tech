package ru.yandex.practicum.payment.model.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.interaction_api.model.payment.dto.PaymentDto;
import ru.yandex.practicum.payment.model.entity.PaymentDao;

@UtilityClass
public class PaymentMapper {

    public static PaymentDto toDto(PaymentDao payment) {
        if (payment == null) {
            return null;
        }

        return PaymentDto.builder()
                .paymentId(payment.getPaymentId())
                .totalPayment(payment.getTotalPayment())
                .totalProduct(payment.getTotalProduct())
                .deliveryTotal(payment.getDeliveryTotal())
                .feeTotal(payment.getFeeTotal())
                .status(payment.getStatus())
                .build();
    }

    public static PaymentDao toEntity(PaymentDto dto) {
        if (dto == null) {
            return null;
        }

        return PaymentDao.builder()
                .paymentId(dto.getPaymentId())
                .totalPayment(dto.getTotalPayment())
                .totalProduct(dto.getTotalProduct())
                .deliveryTotal(dto.getDeliveryTotal())
                .feeTotal(dto.getFeeTotal())
                .status(dto.getStatus())
                .build();
    }
}
