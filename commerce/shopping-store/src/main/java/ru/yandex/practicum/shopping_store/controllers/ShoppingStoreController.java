package ru.yandex.practicum.shopping_store.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction_api.model.enums.ProductCategory;
import ru.yandex.practicum.interaction_api.model.enums.QuantityState;
import ru.yandex.practicum.interaction_api.model.dto.shopping_store.ProductDto;
import ru.yandex.practicum.interaction_api.model.dto.shopping_store.SetProductQuantityStateRequest;
import ru.yandex.practicum.shopping_store.services.ShoppingStoreService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController {

    private final ShoppingStoreService service;

    @GetMapping
    public Page<ProductDto> getProducts(
            @RequestParam ProductCategory category,
            @PageableDefault(sort = {"productName"}) Pageable pageable
    ) {
        return service.getProducts(category, pageable);
    }

    @GetMapping("/{productId}")
    public ProductDto getProductById(@PathVariable UUID productId) {
        return service.getProductById(productId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping
    public ProductDto createProduct(@RequestBody @Valid ProductDto productDto) {
        return service.createProduct(productDto);
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody @Valid ProductDto productDto) {
        return service.updateProduct(productDto);
    }

    @PostMapping("/removeProductFromStore")
    public Boolean removeProduct(@RequestBody UUID productId) {
        return service.removeProduct(productId);
    }

    @PostMapping("/quantityState")
    public Boolean setQuantity(@RequestParam UUID productId, @RequestParam QuantityState quantityState) {
        return service.setQuantity(SetProductQuantityStateRequest.builder()
                .productId(productId)
                .quantityState(quantityState)
                .build());
    }
}
