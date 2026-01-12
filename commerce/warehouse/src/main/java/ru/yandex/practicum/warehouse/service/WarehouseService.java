package ru.yandex.practicum.warehouse.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.interaction_api.model.shopping_cart.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.AddressDto;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.ProductInWarehouseDto;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.request.ShippedToDeliveryRequest;

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
