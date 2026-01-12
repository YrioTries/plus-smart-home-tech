package ru.yandex.practicum.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.error_handler.exception.shopping_store.ProductNotFoundException;
import ru.yandex.practicum.error_handler.exception.payment.PaymentNotFound;
import ru.yandex.practicum.interaction_api.model.order.dto.OrderDto;
import ru.yandex.practicum.interaction_api.model.order.client.OrderClient;
import ru.yandex.practicum.interaction_api.model.payment.dto.PaymentDto;
import ru.yandex.practicum.interaction_api.model.payment.dto.PaymentStatus;
import ru.yandex.practicum.interaction_api.model.shopping_store.dto.ProductDto;
import ru.yandex.practicum.interaction_api.model.shopping_store.client.ShoppingStoreClient;
import ru.yandex.practicum.payment.model.mapper.PaymentMapper;
import ru.yandex.practicum.payment.model.entity.PaymentDao;
import ru.yandex.practicum.payment.model.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;

    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    private final BigDecimal TAX = BigDecimal.valueOf(0.1);

    @Override
    public PaymentDto goToPayment(OrderDto order) {
        PaymentDao newPayment = PaymentDao.builder()
                .totalProduct(order.getProductPrice())
                .deliveryTotal(order.getDeliveryPrice())
                .totalPayment(order.getTotalPrice())
                .feeTotal(order.getTotalPrice().subtract(order.getDeliveryPrice()).subtract(order.getProductPrice()))
                .build();

        return PaymentMapper.toDto(repository.save(newPayment));
    }

    @Override
    public BigDecimal calculateTotalCost(OrderDto order) {

        BigDecimal totalCost = BigDecimal.ZERO;

        BigDecimal deliveryCost = order.getDeliveryPrice();
        BigDecimal productCostWithTax = order.getProductPrice().add(order.getProductPrice().multiply(TAX));

        totalCost = totalCost.add(deliveryCost).add(productCostWithTax);

        return totalCost;
    }

    @Override
    public void createRefund(UUID paymentId) {

        PaymentDao payment = getPayment(paymentId);

        payment.setStatus(PaymentStatus.SUCCESS);
        repository.save(payment);

        OrderDto order = orderClient.getOrderByPayment(paymentId);
        orderClient.paymentOrder(order.getOrderId());

        log.info("Оплата с id {} прошла успешно!", paymentId);
    }

    @Override
    public BigDecimal calculateProductCost(OrderDto order) {

        Map<UUID, Integer> products = order.getProducts();
        BigDecimal productCost = BigDecimal.ZERO;

        for (Map.Entry<UUID, Integer> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Integer quantity = entry.getValue();

            try {
                ProductDto product = shoppingStoreClient.getProductById(productId);

                productCost = productCost.add(
                        product.getPrice().multiply(BigDecimal.valueOf(quantity))
                );

            } catch (ProductNotFoundException e) {
                log.warn("Продукт с id: {} не найден, пропускаю.", productId);
            }
        }

        return productCost;
    }

    @Override
    public void failedPayment(UUID paymentId) {

        PaymentDao payment = getPayment(paymentId);

        payment.setStatus(PaymentStatus.FAILED);
        repository.save(payment);

        OrderDto order = orderClient.getOrderByPayment(paymentId);
        orderClient.failedPaymentOrder(order.getOrderId());

        log.warn("Ошибка при оплате с id {}!", paymentId);
    }

    private PaymentDao getPayment(UUID paymentId) {
        return repository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFound("Оплата с id " + paymentId + " не найдена!"));
    }
}
