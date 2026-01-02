package ru.yandex.practicum.shopping_store.services;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.interaction_api.model.enums.ProductCategory;
import ru.yandex.practicum.interaction_api.model.dto.shopping_store.ProductDto;
import ru.yandex.practicum.interaction_api.model.dto.shopping_store.SetProductQuantityStateRequest;

import java.util.UUID;

@Service
public interface ShoppingStoreService {

    Page<ProductDto> getProducts(ProductCategory category, Pageable pageable);

    ProductDto getProductById(UUID productId);

    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    Boolean removeProduct(UUID productId);

    Boolean setQuantity(SetProductQuantityStateRequest request);
}
