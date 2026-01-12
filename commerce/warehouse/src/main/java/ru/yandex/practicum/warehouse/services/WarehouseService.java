package ru.yandex.practicum.warehouse.services;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.*;
import ru.yandex.practicum.interaction_api.model.dto.shopping_cart.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.request.ShippedToDeliveryRequest;

import java.util.Map;
import java.util.UUID;

@Service
public interface WarehouseService {

    AddressDto getAddress();

    ProductInWarehouseDto addNewProduct(NewProductInWarehouseRequest newProductInWarehouseRequest);

    BookedProductsDto checkQuantityForCart(ShoppingCartDto shoppingCart);

    void acceptProduct(AddProductToWarehouseRequest request);

    void shippedProducts(ShippedToDeliveryRequest request);

    void returnProducts(Map<UUID, Integer> products);

    BookedProductsDto assemblyProducts(AssemblyProductsForOrderRequest request);
}
