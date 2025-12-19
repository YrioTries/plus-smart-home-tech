package ru.yandex.practicum.warehouse.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction_api.model.dto.AddressDto;
import ru.yandex.practicum.interaction_api.model.dto.BookedProductsDto;
import ru.yandex.practicum.interaction_api.model.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.dto.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction_api.model.dto.request.NewProductInWarehouseRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/warehouse")
public class WarehouseController {

    @GetMapping("/address")
    @ResponseStatus(HttpStatus.OK)
    public AddressDto getAddress() {
        return new AddressDto();
    }

    @PostMapping("/check")
    @ResponseStatus(HttpStatus.OK)
    public BookedProductsDto checkProductsWarehouse(@RequestBody ShoppingCartDto shoppingCartDto) {
        return new BookedProductsDto();
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    public void acceptProductToWareHouse(@RequestBody AddProductToWarehouseRequest request) {
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void addProductToWareHouse(@RequestBody NewProductInWarehouseRequest request) {
    }
}
