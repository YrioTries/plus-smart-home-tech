package ru.yandex.practicum.shopping_store.services;


import org.springframework.stereotype.Service;
import ru.yandex.practicum.interaction_api.model.dto.Pageable;
import ru.yandex.practicum.interaction_api.model.dto.ProductDto;
import ru.yandex.practicum.interaction_api.model.dto.request.SetProductQuantityStateRequest;

@Service
public interface ShoppingStoreService {

    ProductDto getPageableListOfProducts(Pageable pageable, String category);

    ProductDto getProductInfo(Long productId);

    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    Boolean setProductStatus(SetProductQuantityStateRequest request);

    Boolean deleteProduct(String productId);
}
