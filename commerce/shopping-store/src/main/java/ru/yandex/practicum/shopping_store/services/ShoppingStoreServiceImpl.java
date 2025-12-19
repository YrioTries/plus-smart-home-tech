package ru.yandex.practicum.shopping_store.services;

import ru.yandex.practicum.interaction_api.model.dto.Pageable;
import ru.yandex.practicum.interaction_api.model.dto.ProductDto;
import ru.yandex.practicum.interaction_api.model.dto.request.SetProductQuantityStateRequest;

public class ShoppingStoreServiceImpl implements ShoppingStoreService{

    public ProductDto getPageableListOfProducts(Pageable pageable, String category) {
        return new ProductDto();
    }

    public ProductDto getProductInfo(Long productId) {
        return new ProductDto();
    }

    public ProductDto createProduct(ProductDto productDto) {
        return new ProductDto();
    }

    public ProductDto updateProduct(ProductDto productDto) {
        return new ProductDto();
    }

    public Boolean setProductStatus(SetProductQuantityStateRequest request) {
        return true;
    }

    public Boolean deleteProduct(String productId) {
        return true;
    }
}
