package ru.yandex.practicum.shopping_store.services;


import org.springframework.stereotype.Service;
import ru.yandex.practicum.interaction_api.enums.ProductCategory;
import ru.yandex.practicum.interaction_api.enums.QuantityState;
import ru.yandex.practicum.interaction_api.model.dto.Pageable;
import ru.yandex.practicum.interaction_api.model.dto.ProductDto;
import ru.yandex.practicum.interaction_api.model.dto.request.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

@Service
public interface ShoppingStoreService {

    List<ProductDto> getPageableListOfProducts(Pageable pageable, ProductCategory category);

    ProductDto getProductInfo(UUID productId);

    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    Boolean setProductQuantityState(UUID productId, QuantityState quantityState);

    Boolean deleteProduct(UUID productId);
}
