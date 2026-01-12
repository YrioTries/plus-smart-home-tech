package ru.yandex.practicum.warehouse.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.error_handler.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.error_handler.exception.ProductInWarehouseNotFoundException;
import ru.yandex.practicum.error_handler.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.error_handler.exception.warehouse.ProductLowQuantityInWarehouse;
import ru.yandex.practicum.error_handler.exception.warehouse.booking.NotOrderBookingFound;
import ru.yandex.practicum.interaction_api.model.dto.delivery.client.DeliveryClient;
import ru.yandex.practicum.interaction_api.model.dto.order.client.OrderClient;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.*;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.DimensionDto;
import ru.yandex.practicum.interaction_api.model.dto.shopping_cart.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.request.ShippedToDeliveryRequest;
import ru.yandex.practicum.warehouse.WarehouseApplication;
import ru.yandex.practicum.warehouse.model.entity.OrderBooking;
import ru.yandex.practicum.warehouse.model.entity.ProductInWarehouseDao;
import ru.yandex.practicum.warehouse.model.mapper.OrderBookingMapper;
import ru.yandex.practicum.warehouse.model.mapper.ProductInWarehouseMapper;
import ru.yandex.practicum.warehouse.model.repositories.OrderBookingRepository;
import ru.yandex.practicum.warehouse.model.repositories.WarehouseRepository;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final OrderBookingRepository orderBookingRepository;

    private final OrderClient orderClient;

    @Override
    public ProductInWarehouseDto addNewProduct(NewProductInWarehouseRequest newProduct) {
        if (isProductInWarehouse(newProduct.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("Продукт с id " + newProduct.getProductId() + " уже добавлен на склад!");
        }

        return ProductInWarehouseMapper.toDto(warehouseRepository.save(ProductInWarehouseMapper.toEntity(newProduct)));
    }

    @Override
    public BookedProductsDto checkQuantityForCart(ShoppingCartDto shoppingCart) {
        BookedProductsDto bookedProductsDto = BookedProductsDto.builder().build();

        shoppingCart.getProducts().forEach((productId, quantity) -> {
            ProductInWarehouseDao productInWarehouse = getProductInWarehouse(productId);

            if (quantity > productInWarehouse.getQuantity()) {
                throw new ProductInShoppingCartLowQuantityInWarehouse("Товара с id " + productId + " в корзине больше, чем доступно на складе!");
            }

            bookedProductsDto.setDeliveryWeight(bookedProductsDto.getDeliveryWeight()+ productInWarehouse.getWeight());
            bookedProductsDto.setDeliveryVolume(bookedProductsDto.getDeliveryVolume()+calculateVolume(productInWarehouse));
        });

        return bookedProductsDto;
    }

    @Override
    public void acceptProduct(AddProductToWarehouseRequest request) {

        ProductInWarehouseDao productInWarehouse = getProductInWarehouse(request.getProductId());
        productInWarehouse.setQuantity(productInWarehouse.getQuantity()+request.getQuantity());

        warehouseRepository.save(productInWarehouse);

        log.info("Продукт с id {} в количестве {} принят на склад!", productInWarehouse.getProductId(), productInWarehouse.getQuantity());
    }

    @Override
    public AddressDto getAddress() {
        return WarehouseApplication.getRandomAddress();
    }

    @Override
    public void shippedProducts(ShippedToDeliveryRequest request) {

        OrderBooking orderBooking = orderBookingRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NotOrderBookingFound("Забронированные товары для заказа с id " + request.getOrderId() + " не найдены!"));

        orderBooking.setDeliveryId(request.getDeliveryId());
        orderBookingRepository.save(orderBooking);

        log.info("Товары для заказа с id {} переданы в доставку!", request.getOrderId());
    }

    @Override
    @Transactional
    public void returnProducts(Map<UUID, Integer> products) {

        for (Map.Entry<UUID, Integer> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Integer quantity = entry.getValue();

            try {
                ProductInWarehouseDao product = getProductInWarehouse(productId);

                product.setQuantity(product.getQuantity() + quantity);
                warehouseRepository.save(product);

            } catch (ProductInWarehouseNotFoundException e) {
                log.warn("Продукт с id {} не найден на складе, пропускаем!", productId);
            }
        }

        log.info("Товары успешно вернулись на склад!");
    }

    @Override
    @Transactional
    public BookedProductsDto assemblyProducts(AssemblyProductsForOrderRequest request) {

        Double deliveryWeight = 0.0;
        double deliveryVolume = 0.0;
        boolean fragile = false;

        for(Map.Entry<UUID, Integer> entry : request.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Integer quantity = entry.getValue();

            try {
                ProductInWarehouseDao product = getProductInWarehouse(productId);

                if (product.getQuantity() < quantity) {
                    throw new ProductLowQuantityInWarehouse("Товара с id " + productId + " на складе меньше, чем запрашивается!");
                }

                product.setQuantity(product.getQuantity() - quantity);
                warehouseRepository.save(product);

                log.info("Остаток товара с id: {} на складе: {}.", productId, product.getQuantity().toString());

                deliveryWeight += product.getWeight();
                deliveryVolume += calculateVolume(product);

                if (product.getFragile()) {
                    fragile = true;
                }

            } catch (ProductInWarehouseNotFoundException e) {
                throw new SpecifiedProductAlreadyInWarehouseException("Товар с id " + productId + " не найден на складе!");
            }
        }

        OrderBooking newOrderBooking = OrderBooking.builder()
                .products(request.getProducts())
                .deliveryWeight(deliveryWeight)
                .deliveryVolume(deliveryVolume)
                .fragile(fragile)
                .build();

        orderClient.assemblyOrder(request.getOrderId());

        return OrderBookingMapper.toDto(orderBookingRepository.save(newOrderBooking));
    }

    private boolean isProductInWarehouse(UUID productId) {
        return warehouseRepository.existsById(productId);
    }

    private Double calculateVolume(ProductInWarehouseDao product) {
        DimensionDto dimension = product.getDimension();
        return dimension.getHeight()*dimension.getDepth()*dimension.getWidth();
    }

    private ProductInWarehouseDao getProductInWarehouse(UUID productId) {
        return warehouseRepository.findById(productId)
                .orElseThrow(() -> new ProductInWarehouseNotFoundException("Продукт с id " + productId + " не найден на складе!"));
    }
}
