package ru.yandex.practicum.warehouse.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction_api.clients.WarehouseClient;
import ru.yandex.practicum.interaction_api.model.dto.AddressDto;
import ru.yandex.practicum.interaction_api.model.dto.BookedProductsDto;
import ru.yandex.practicum.interaction_api.model.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.dto.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction_api.model.dto.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.services.WarehouseService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/warehouse")
public class WarehouseController implements WarehouseClient {

    private final WarehouseService warehouseService;

    @Override
    @GetMapping("/address")
    public AddressDto getAddress() {
        return warehouseService.getAddress();
    }

    @Override
    @PostMapping(value = "/check", consumes = "application/json", produces = "application/json")
    public BookedProductsDto checkProductsWarehouse(@RequestBody ShoppingCartDto shoppingCartDto) {
        return warehouseService.checkProductsWarehouse(shoppingCartDto);
    }

    @Override
    @PostMapping(value = "/add", consumes = "application/json")
    public void acceptProductToWareHouse(@RequestBody AddProductToWarehouseRequest request) {
        warehouseService.acceptProductToWareHouse(request);
    }

    @Override
    @PutMapping(consumes = "application/json")
    public void addProductToWareHouse(@RequestBody NewProductInWarehouseRequest request) {
        warehouseService.addProductToWareHouse(request);
    }
}
