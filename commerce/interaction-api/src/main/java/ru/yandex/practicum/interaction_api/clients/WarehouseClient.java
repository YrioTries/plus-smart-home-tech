package ru.yandex.practicum.interaction_api.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction_api.model.dto.AddressDto;
import ru.yandex.practicum.interaction_api.model.dto.BookedProductsDto;
import ru.yandex.practicum.interaction_api.model.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.dto.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction_api.model.dto.request.NewProductInWarehouseRequest;

@FeignClient(
        name = "warehouse",
        path = "/api/v1/warehouse"
)
public interface WarehouseClient {

    @GetMapping("/address")
    AddressDto getAddress();

    @PostMapping("/check")
    BookedProductsDto checkProductsWarehouse(
            @RequestBody ShoppingCartDto shoppingCartDto);

    @PostMapping("/add")
    void acceptProductToWareHouse(@RequestBody AddProductToWarehouseRequest request);

    @PutMapping
    void addProductToWareHouse(@RequestBody NewProductInWarehouseRequest request);
}
