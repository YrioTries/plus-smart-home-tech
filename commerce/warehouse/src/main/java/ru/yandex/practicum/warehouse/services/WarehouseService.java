package ru.yandex.practicum.warehouse.services;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.*;
import ru.yandex.practicum.interaction_api.model.dto.shopping_cart.ShoppingCartDto;

@Service
public interface WarehouseService {

    ProductInWarehouseDto addNewProduct(NewProductInWarehouseRequest newProductInWarehouseRequest);

    BookedProductsDto checkQuantityForCart(ShoppingCartDto shoppingCart);

    void acceptProduct(AddProductToWarehouseRequest request);

    AddressDto getAddress();
}
