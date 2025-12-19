package ru.yandex.practicum.warehouse.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction_api.model.dto.Pageable;
import ru.yandex.practicum.interaction_api.model.dto.ProductDto;
import ru.yandex.practicum.interaction_api.model.dto.request.SetProductQuantityStateRequest;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreClient {

    @GetMapping
    ProductDto getPageableListOfProducts(@RequestBody Pageable pageable, @RequestBody String category);

    @GetMapping("/{productId}")
    ProductDto getProductInfo(@PathVariable Long productId);

    @PutMapping
    ProductDto createProduct(@RequestBody ProductDto productDto);

    @PostMapping
    ProductDto updateProduct(@RequestBody ProductDto productDto);

    @PostMapping("/quantityState")
    Boolean setProductStatus(@RequestBody SetProductQuantityStateRequest request);

    @PostMapping("/removeProductFromStore")
    Boolean deleteProduct(@RequestBody String productId);
}
