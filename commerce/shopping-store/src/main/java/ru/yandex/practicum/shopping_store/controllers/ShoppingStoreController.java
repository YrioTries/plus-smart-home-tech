package ru.yandex.practicum.shopping_store.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction_api.model.dto.Pageable;
import ru.yandex.practicum.interaction_api.model.dto.ProductDto;
import ru.yandex.practicum.interaction_api.model.dto.request.SetProductQuantityStateRequest;
import ru.yandex.practicum.shopping_store.services.ShoppingStoreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/shopping-store")
public class ShoppingStoreController {

    private final ShoppingStoreService shoppingStoreService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDto> getPageableListOfProducts(
            @RequestParam String category,
            @ModelAttribute Pageable pageable) {
        return shoppingStoreService.getPageableListOfProducts(pageable, category);
    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDto getProductInfo(@PathVariable("productId") String productId) {
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
    public Boolean setProductQuantityState( @RequestParam SetProductQuantityStateRequest request) {
        return shoppingStoreService.setProductQuantityState(request);
    }

    @PostMapping("/removeProductFromStore")
    @ResponseStatus(HttpStatus.OK)
    public Boolean deleteProduct(@RequestBody String productId) {
        return shoppingStoreService.deleteProduct(productId);
    }
}
