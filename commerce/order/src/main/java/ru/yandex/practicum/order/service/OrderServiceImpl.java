package ru.yandex.practicum.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.error_handler.exception.warehouse.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.error_handler.exception.NotAuthorizedUserException;
import ru.yandex.practicum.error_handler.exception.order.NoOrderFoundException;
import ru.yandex.practicum.error_handler.exception.warehouse.ProductLowQuantityInWarehouse;
import ru.yandex.practicum.interaction_api.model.warehouse.client.WarehouseClient;
import ru.yandex.practicum.interaction_api.model.delivery.dto.DeliveryDto;
import ru.yandex.practicum.interaction_api.model.delivery.client.DeliveryClient;
import ru.yandex.practicum.interaction_api.model.order.dto.request.CreateNewOrderRequest;
import ru.yandex.practicum.interaction_api.model.order.dto.OrderDto;
import ru.yandex.practicum.interaction_api.model.order.dto.OrderState;
import ru.yandex.practicum.interaction_api.model.order.dto.request.ProductReturnRequest;
import ru.yandex.practicum.interaction_api.model.payment.dto.PaymentDto;
import ru.yandex.practicum.interaction_api.model.payment.client.PaymentClient;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.order.model.entity.OrderDao;
import ru.yandex.practicum.order.model.mapper.OrderMapper;
import ru.yandex.practicum.order.model.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;

    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;
    private final WarehouseClient warehouseClient;

    @Override
    public Page<OrderDto> getOrder(String username, Pageable pageable) {
        if (username == null) {
            throw new NotAuthorizedUserException("Поле username не может быть пустым!");
        }

        Page<OrderDao> orders = repository.findByUsername(username, pageable);
        return orders.map(OrderMapper::toDto);
    }

    @Override
    public OrderDto getOrderByPayment(UUID paymentId) {
        return OrderMapper.toDto(repository.findByPaymentId(paymentId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ с id оплаты " + paymentId + " не найден!")));
    }

    @Override
    public OrderDto getOrderByDelivery(UUID deliveryId) {
        return OrderMapper.toDto(repository.findByDeliveryId(deliveryId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ с id доставки " + deliveryId + " не найден!")));
    }

    @Override
    @Transactional
    public OrderDto createOrder(String username, CreateNewOrderRequest request) {

        if (username == null) {
            throw new NotAuthorizedUserException("Поле username не может быть пустым!");
        }

        OrderDao newOrder = OrderDao.builder()
                .shoppingCartId(request.getShoppingCart().getShoppingCartId())
                .products(request.getShoppingCart().getProducts())
                .username(username)
                .build();

        OrderDao order = saveOrder(newOrder);

        try {
            BookedProductsDto bookedProducts = warehouseClient.assemblyProducts(AssemblyProductsForOrderRequest.builder()
                    .products(request.getShoppingCart().getProducts())
                    .orderId(order.getOrderId())
                    .build());

            order.setDeliveryWeight(bookedProducts.getDeliveryWeight());
            order.setDeliveryVolume(bookedProducts.getDeliveryVolume());
            order.setFragile(bookedProducts.getFragile());

            order.setProductPrice(paymentClient.calculateProductCost(OrderMapper.toDto(order)));

            DeliveryDto delivery = deliveryClient.createDelivery(
                    DeliveryDto.builder()
                            .fromAddress(warehouseClient.getWarehouseAddress())
                            .toAddress(request.getDeliveryAddress())
                            .orderId(order.getOrderId())
                            .build()
            );
            order.setDeliveryId(delivery.getDeliveryId());

            BigDecimal deliveryPrice =
                    deliveryClient.calculateDeliveryCost(OrderMapper.toDto(order));
            order.setDeliveryPrice(deliveryPrice);

            BigDecimal totalPrice =
                    paymentClient.calculateTotalCost(OrderMapper.toDto(order));
            order.setTotalPrice(totalPrice);

            PaymentDto payment = paymentClient.goToPayment(OrderMapper.toDto(order));
            order.setPaymentId(payment.getPaymentId());

            repository.save(order);

            paymentClient.refund(payment.getPaymentId());

            return OrderMapper.toDto(getOrder(order.getOrderId()));
        } catch (ProductLowQuantityInWarehouse e) {
            repository.delete(order);
            throw new ProductLowQuantityInWarehouse(e.getMessage());
        } catch (NoSpecifiedProductInWarehouseException e){
            repository.delete(order);
            throw new NoSpecifiedProductInWarehouseException(e.getMessage());
        } catch (Exception e) {
            repository.delete(order);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public OrderDto returnOrder(ProductReturnRequest request) {

        OrderDao order = getOrder(request.getOrderId());

        warehouseClient.returnProducts(request.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);

        return OrderMapper.toDto(repository.save(order));
    }

    @Override
    @Transactional
    public OrderDto paymentOrder(UUID orderId) {

        OrderDao order = getOrder(orderId);
        order.setState(OrderState.PAID);

        return OrderMapper.toDto(repository.save(order));
    }

    @Override
    @Transactional
    public OrderDto failedPaymentOrder(UUID orderId) {

        OrderDao order = getOrder(orderId);
        order.setState(OrderState.PAYMENT_FAILED);

        return OrderMapper.toDto(repository.save(order));
    }

    @Override
    @Transactional
    public OrderDto deliveryOrder(UUID orderId) {

        OrderDao order = getOrder(orderId);
        order.setState(OrderState.DELIVERED);

        return OrderMapper.toDto(repository.save(order));
    }

    @Override
    @Transactional
    public OrderDto failedDeliveryOrder(UUID orderId) {

        OrderDao order = getOrder(orderId);
        order.setState(OrderState.DELIVERY_FAILED);

        return OrderMapper.toDto(repository.save(order));
    }

    @Override
    @Transactional
    public OrderDto completedOrder(UUID orderId) {

        OrderDao order = getOrder(orderId);
        order.setState(OrderState.COMPLETED);

        return OrderMapper.toDto(repository.save(order));
    }

    @Override
    @Transactional
    public OrderDto calculateTotalOrder(UUID orderId) {

        OrderDao order = getOrder(orderId);
        order.setTotalPrice(paymentClient.calculateTotalCost(OrderMapper.toDto(order)));

        return OrderMapper.toDto(repository.save(order));
    }

    @Override
    @Transactional
    public OrderDto calculateDeliveryOrder(UUID orderId) {

        OrderDao order = getOrder(orderId);
        order.setDeliveryPrice(deliveryClient.calculateDeliveryCost(OrderMapper.toDto(order)));

        return OrderMapper.toDto(repository.save(order));
    }

    @Override
    @Transactional
    public OrderDto assemblyOrder(UUID orderId) {

        OrderDao order = getOrder(orderId);
        order.setState(OrderState.ASSEMBLED);

        return OrderMapper.toDto(repository.save(order));
    }

    @Override
    @Transactional
    public OrderDto failedAssemblyOrder(UUID orderId) {
        OrderDao order = getOrder(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);

        return OrderMapper.toDto(repository.save(order));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OrderDao saveOrder(OrderDao newOrder) {
        return repository.save(newOrder);
    }

    private OrderDao getOrder(UUID orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ с id " + orderId + " не найден!"));
    }
}
