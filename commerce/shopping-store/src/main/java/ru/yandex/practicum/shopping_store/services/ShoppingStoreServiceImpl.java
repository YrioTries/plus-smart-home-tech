package ru.yandex.practicum.shopping_store.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.error_handler.exception.shopping_store.ProductNotFoundException;
import ru.yandex.practicum.interaction_api.model.shopping_store.dto.ProductCategory;
import ru.yandex.practicum.interaction_api.model.shopping_store.dto.ProductState;
import ru.yandex.practicum.interaction_api.model.shopping_store.dto.ProductDto;
import ru.yandex.practicum.interaction_api.model.shopping_store.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.shopping_store.model.entity.ProductDao;
import ru.yandex.practicum.shopping_store.model.mapper.ProductMapper;
import ru.yandex.practicum.shopping_store.model.repository.ShoppingStoreRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingStoreServiceImpl implements ShoppingStoreService {

    private final ShoppingStoreRepository repository;

    @Override
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        Page<ProductDao> products = repository.findAllByProductCategory(category, pageable);
        return products.map(ProductMapper::toDto);
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        return ProductMapper.toDto(productExists(productId));
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        ProductDao newProductDao = ProductMapper.toEntity(productDto);
        return ProductMapper.toDto(repository.save(newProductDao));
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        ProductDao oldProductDao = productExists(productDto.getProductId());
        return ProductMapper.toDto(repository.save(ProductMapper.updateFields(oldProductDao, productDto)));
    }

    @Override
    public Boolean removeProduct(UUID productId) {
        ProductDao productDao = productExists(productId);

        productDao.setProductState(ProductState.DEACTIVATE);
        repository.save(productDao);
        return true;
    }

    @Override
    public Boolean setQuantity(SetProductQuantityStateRequest request) {
        ProductDao productDao = productExists(request.getProductId());

        productDao.setQuantityState(request.getQuantityState());
        repository.save(productDao);
        return true;
    }

    private ProductDao productExists(UUID productId) {
        try {
            return repository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Товар с id " + productId + " не найден!"));
        } catch (ProductNotFoundException e) {
            log.error("Ошибка поиска товара с id {}: ", productId, e);
            throw e;
        }
    }
}
