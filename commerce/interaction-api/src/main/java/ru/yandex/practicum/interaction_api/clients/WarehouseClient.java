package ru.yandex.practicum.interaction_api.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.AddressDto;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction_api.model.dto.shopping_cart.ShoppingCartDto;

@FeignClient(name = "warehouse")
public interface WarehouseClient {

    @GetMapping("/api/v1/warehouse/address")
    AddressDto getWarehouseAddress();

    @PostMapping ("/api/v1/warehouse/check")
    BookedProductsDto checkQuantityForCart(@RequestBody ShoppingCartDto shoppingCartDto);
}
