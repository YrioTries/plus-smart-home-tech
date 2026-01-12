package ru.yandex.practicum.warehouse.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction_api.model.shopping_cart.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.AddressDto;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.ProductInWarehouseDto;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.request.ShippedToDeliveryRequest;
import ru.yandex.practicum.warehouse.services.WarehouseService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController {

    private final WarehouseService service;

    @GetMapping("/address")
    public AddressDto getAddress() {
        return service.getAddress();
    }

    @PutMapping
    public ProductInWarehouseDto addNewProduct(@RequestBody NewProductInWarehouseRequest newProductInWarehouseRequest) {
        return service.addNewProduct(newProductInWarehouseRequest);
    }

    @PostMapping("/check")
    public BookedProductsDto checkQuantityForCart(@RequestBody ShoppingCartDto shoppingCart) {
        return service.checkQuantityForCart(shoppingCart);
    }

    @PostMapping("/add")
    public void acceptProduct(@RequestBody @Valid AddProductToWarehouseRequest request) {
        service.acceptProduct(request);
    }

    @PostMapping("/shipped")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void shippedOrder(@RequestBody @Valid ShippedToDeliveryRequest request) {
        service.shippedProducts(request);
    }

    @PostMapping("/returnProducts")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void returnProducts(@RequestBody @Valid Map<UUID, Integer> products) {
        service.returnProducts(products);
    }

    @PostMapping("/assembly")
    public BookedProductsDto assemblyProducts(@RequestBody AssemblyProductsForOrderRequest request) {
        return service.assemblyProducts(request);
    }
}