package ru.yandex.practicum.warehouse.services;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.interaction_api.model.dto.AddressDto;
import ru.yandex.practicum.interaction_api.model.dto.BookedProductsDto;
import ru.yandex.practicum.interaction_api.model.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.dto.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction_api.model.dto.request.NewProductInWarehouseRequest;

@Service
public class WarehouseServiceImpl implements WarehouseService{

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
