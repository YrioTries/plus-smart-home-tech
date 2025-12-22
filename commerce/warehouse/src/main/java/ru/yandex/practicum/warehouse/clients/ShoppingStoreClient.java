package ru.yandex.practicum.warehouse.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction_api.model.dto.Pageable;
import ru.yandex.practicum.interaction_api.model.dto.ProductDto;
import ru.yandex.practicum.interaction_api.model.dto.request.SetProductQuantityStateRequest;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreClient {

    @GetMapping
    ProductDto getPageableListOfProducts(@RequestParam Pageable pageable, @RequestParam String category);

    @GetMapping("/{productId}")
    ProductDto getProductInfo(@PathVariable("productId") String productId);

    @PutMapping
    ProductDto createProduct(@RequestBody ProductDto productDto);

    @PostMapping
    ProductDto updateProduct(@RequestBody ProductDto productDto);

    @PostMapping("/quantityState")
    Boolean setProductStatus(@RequestBody SetProductQuantityStateRequest request);

    @PostMapping("/removeProductFromStore")
    Boolean deleteProduct(@RequestBody String productId);
}
