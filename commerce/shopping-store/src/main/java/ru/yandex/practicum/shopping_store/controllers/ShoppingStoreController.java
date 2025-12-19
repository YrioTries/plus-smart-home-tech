package ru.yandex.practicum.shopping_store.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction_api.model.dto.Pageable;
import ru.yandex.practicum.interaction_api.model.dto.ProductDto;
import ru.yandex.practicum.interaction_api.model.dto.request.SetProductQuantityStateRequest;

import java.util.ArrayList;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/shopping-store")
public class ShoppingStoreController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ProductDto getPageableListOfProducts(Pageable pageable, @RequestBody String category) {
        return new ProductDto();
    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDto getProductInfo(@PathVariable Long productId) {
        return new ProductDto();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createProduct(@RequestBody ProductDto productDto) {
        return new ProductDto();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        return new ProductDto();
    }

    @PostMapping("/quantityState")
    @ResponseStatus(HttpStatus.OK)
    public Boolean setProductStatus(@RequestBody SetProductQuantityStateRequest request) {
        return true;
    }

    @PostMapping("/removeProductFromStore")
    @ResponseStatus(HttpStatus.OK)
    public Boolean deleteProduct(@RequestBody String productId) {
        return true;
    }
}
