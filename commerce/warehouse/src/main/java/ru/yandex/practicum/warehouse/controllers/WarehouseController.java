package ru.yandex.practicum.warehouse.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction_api.model.dto.AddressDto;
import ru.yandex.practicum.interaction_api.model.dto.BookedProductsDto;
import ru.yandex.practicum.interaction_api.model.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.dto.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction_api.model.dto.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.services.WarehouseService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/warehouse")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping("/address")
    @ResponseStatus(HttpStatus.OK)
    public AddressDto getAddress() {
        return warehouseService.getAddress();
    }

    @PostMapping("/check")
    @ResponseStatus(HttpStatus.OK)
    public BookedProductsDto checkProductsWarehouse(@RequestBody ShoppingCartDto shoppingCartDto) {
        return warehouseService.checkProductsWarehouse(shoppingCartDto);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    public void acceptProductToWareHouse(@RequestBody AddProductToWarehouseRequest request) {
        warehouseService.acceptProductToWareHouse(request);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void addProductToWareHouse(@RequestBody NewProductInWarehouseRequest request) {
        warehouseService.addProductToWareHouse(request);
    }
}
