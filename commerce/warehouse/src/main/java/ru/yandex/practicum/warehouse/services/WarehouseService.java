package ru.yandex.practicum.warehouse.services;

import ru.yandex.practicum.interaction_api.model.dto.AddressDto;
import ru.yandex.practicum.interaction_api.model.dto.BookedProductsDto;
import ru.yandex.practicum.interaction_api.model.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.dto.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction_api.model.dto.request.NewProductInWarehouseRequest;

public interface WarehouseService {

    AddressDto getAddress();

    BookedProductsDto checkProductsWarehouse(ShoppingCartDto shoppingCartDto);

    void acceptProductToWareHouse(AddProductToWarehouseRequest request);

    void addProductToWareHouse(NewProductInWarehouseRequest request);
}
