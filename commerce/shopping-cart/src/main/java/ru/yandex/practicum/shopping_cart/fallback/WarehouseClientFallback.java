package ru.yandex.practicum.shopping_cart.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction_api.clients.WarehouseClient;
import ru.yandex.practicum.interaction_api.model.dto.AddressDto;
import ru.yandex.practicum.interaction_api.model.dto.BookedProductsDto;
import ru.yandex.practicum.interaction_api.model.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.dto.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction_api.model.dto.request.NewProductInWarehouseRequest;

@Slf4j
@Component
public class WarehouseClientFallback implements WarehouseClient {

    @Override
    public AddressDto getAddress() {
        // Например, вернуть "неизвестный" адрес или null
        return null;
    }

    @Override
    public BookedProductsDto checkProductsWarehouse(ShoppingCartDto shoppingCartDto) {
        BookedProductsDto dto = new BookedProductsDto();
        dto.setDeliveryWeight(0.0);
        dto.setDeliveryVolume(0.0);
        dto.setFragile(false);
        return dto;
    }

    @Override
    public void acceptProductToWareHouse(AddProductToWarehouseRequest request) {
        // Ничего не делаем или логируем
    }

    @Override
    public void addProductToWareHouse(NewProductInWarehouseRequest request) {
        // Ничего не делаем или логируем
    }
}
