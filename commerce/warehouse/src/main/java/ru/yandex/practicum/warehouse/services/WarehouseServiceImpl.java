package ru.yandex.practicum.warehouse.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.interaction_api.model.dto.AddressDto;
import ru.yandex.practicum.interaction_api.model.dto.BookedProductsDto;
import ru.yandex.practicum.interaction_api.model.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.dto.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction_api.model.dto.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.clients.ShoppingCartClient;
import ru.yandex.practicum.warehouse.clients.ShoppingStoreClient;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService{
    private final ShoppingCartClient cartClient;
    private final ShoppingStoreClient storeClient;

    public AddressDto getAddress() {
        return new AddressDto();
    }

    public BookedProductsDto checkProductsWarehouse(ShoppingCartDto shoppingCartDto) {
        return new BookedProductsDto();
    }

    public void acceptProductToWareHouse(AddProductToWarehouseRequest request) {
    }

    public void addProductToWareHouse(NewProductInWarehouseRequest request) {
    }
}
