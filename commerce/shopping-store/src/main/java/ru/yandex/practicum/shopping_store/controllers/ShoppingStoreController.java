package ru.yandex.practicum.shopping_store.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction_api.enums.ProductCategory;
import ru.yandex.practicum.interaction_api.enums.QuantityState;
import ru.yandex.practicum.interaction_api.model.dto.ProductDto;
import ru.yandex.practicum.shopping_store.services.ShoppingStoreService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/shopping-store")
public class ShoppingStoreController {

    private final ShoppingStoreService shoppingStoreService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ProductDto> getProducts(
            @RequestParam ProductCategory category,
            @PageableDefault(sort = {"productName"}) Pageable pageable
    ) {
        return shoppingStoreService.getProducts(category, pageable);
    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDto getProductInfo(@PathVariable UUID productId) {
        return shoppingStoreService.getProductInfo(productId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createProduct(@RequestBody ProductDto productDto) {
        return shoppingStoreService.createProduct(productDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        return shoppingStoreService.updateProduct(productDto);
    }

    @PostMapping("/quantityState")
    @ResponseStatus(HttpStatus.OK)
    public boolean setProductQuantityState(@RequestParam UUID productId,
                                           @RequestParam QuantityState quantityState) {
        return shoppingStoreService.setProductQuantityState(productId, quantityState);
    }

    @PostMapping("/removeProductFromStore")
    @ResponseStatus(HttpStatus.OK)
    public Boolean deleteProduct(@RequestBody UUID productId) {
        return shoppingStoreService.deleteProduct(productId);
    }
}
