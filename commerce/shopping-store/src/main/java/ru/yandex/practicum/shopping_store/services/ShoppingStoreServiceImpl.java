package ru.yandex.practicum.shopping_store.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.error_handler.exception.ProductNotFoundException;
import ru.yandex.practicum.interaction_api.model.enums.ProductCategory;
import ru.yandex.practicum.interaction_api.model.enums.ProductState;
import ru.yandex.practicum.interaction_api.model.dto.shopping_store.ProductDto;
import ru.yandex.practicum.interaction_api.model.dto.shopping_store.SetProductQuantityStateRequest;
import ru.yandex.practicum.shopping_store.entity.Product;
import ru.yandex.practicum.shopping_store.entity.ProductMapper;
import ru.yandex.practicum.shopping_store.repositories.ShoppingStoreRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingStoreServiceImpl implements ShoppingStoreService {

    private final ShoppingStoreRepository repository;

    @Override
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        Page<Product> products = repository.findAllByProductCategory(category, pageable);
        return products.map(ProductMapper::toDto);
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        return ProductMapper.toDto(productExists(productId));
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        Product newProduct = ProductMapper.toEntity(productDto);
        return ProductMapper.toDto(repository.save(newProduct));
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        Product oldProduct = productExists(productDto.getProductId());
        return ProductMapper.toDto(repository.save(ProductMapper.updateFields(oldProduct, productDto)));
    }

    @Override
    public Boolean removeProduct(UUID productId) {
        Product product = productExists(productId);

        product.setProductState(ProductState.DEACTIVATE);
        repository.save(product);
        return true;
    }

    @Override
    public Boolean setQuantity(SetProductQuantityStateRequest request) {
        Product product = productExists(request.getProductId());

        product.setQuantityState(request.getQuantityState());
        repository.save(product);
        return true;
    }

    private Product productExists(UUID productId) {
        try {
            return repository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Товар с id " + productId + " не найден!"));
        } catch (ProductNotFoundException e) {
            log.error("Ошибка поиска товара с id {}: ", productId, e);
            throw e;
        }
    }
}
