package ru.yandex.practicum.interaction_api.model.dto.shopping_store.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.interaction_api.model.dto.shopping_store.ProductDto;

import java.util.UUID;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreClient {

    @GetMapping("/api/v1/shopping-store/{productId}")
    ProductDto getProductById(@PathVariable("productId") UUID productId);
}
