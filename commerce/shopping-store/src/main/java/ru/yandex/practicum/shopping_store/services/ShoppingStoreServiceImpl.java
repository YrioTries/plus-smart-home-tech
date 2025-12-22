package ru.yandex.practicum.shopping_store.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.interaction_api.enums.ProductCategory;
import ru.yandex.practicum.interaction_api.enums.ProductState;
import ru.yandex.practicum.interaction_api.enums.QuantityState;
import ru.yandex.practicum.interaction_api.exception.ProductNotFoundException;
import ru.yandex.practicum.interaction_api.model.dto.Pageable;
import ru.yandex.practicum.interaction_api.model.dto.ProductDto;
import ru.yandex.practicum.interaction_api.model.dto.request.SetProductQuantityStateRequest;
import ru.yandex.practicum.interaction_api.model.entity.ProductEntity;
import ru.yandex.practicum.interaction_api.model.mappers.ProductMapper;
import ru.yandex.practicum.shopping_store.repositories.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShoppingStoreServiceImpl implements ShoppingStoreService{

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    private ProductEntity getProductOrThrow(String productId) {
        return productRepository
                .findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Товар с id:" + productId +"не найден"));
    }

    @Override
    public List<ProductDto> getPageableListOfProducts(Pageable pageable, String category) {
        List<ProductEntity> entities = productRepository
                .findByProductCategory(ProductCategory.valueOf(category));

        return entities
                .stream()
                .filter(p -> p.getProductState() == ProductState.ACTIVE)
                .skip((long) pageable.getPage() * pageable.getSize())
                .limit(pageable.getSize())
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    public ProductDto getProductInfo(String productId) {
        ProductEntity productEntity = getProductOrThrow(productId);
        return productMapper.toDto(productEntity);
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        ProductEntity productEntity = productRepository
                .findById(productDto.getProductId()).orElseGet(() -> {
            ProductEntity entity = new ProductEntity();
            entity.setId(productDto.getProductId());
            entity.setName(productDto.getProductName());
            entity.setDescription(productDto.getDescription());
            entity.setImageSrc(productDto.getImageSrc());
            entity.setQuantityState(productDto.getQuantityState());
            entity.setProductState(productDto.getProductState());
            entity.setProductCategory(productDto.getProductCategory());
            entity.setPrice(productDto.getPrice());
            return entity;
        });

        if (productEntity.getProductState() == null)
            productEntity.setProductState(ProductState.ACTIVE);

        productRepository.save(productEntity);

        return productMapper.toDto(productEntity);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        ProductEntity productEntity = getProductOrThrow(productDto.getProductId());

        productEntity.setName(productDto.getProductName());
        productEntity.setDescription(productDto.getDescription());
        productEntity.setImageSrc(productDto.getImageSrc());
        productEntity.setQuantityState(productDto.getQuantityState());
        productEntity.setProductState(productDto.getProductState());
        productEntity.setProductCategory(productDto.getProductCategory());
        productEntity.setPrice(productDto.getPrice());

        productRepository.save(productEntity);

        return productMapper.toDto(productEntity);
    }

    @Override
    public Boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        ProductEntity productEntity = getProductOrThrow(request.getProductId());

        productEntity.setQuantityState(QuantityState.valueOf(request.getQuantityState()));
        productRepository.save(productEntity);
        return true;
    }

    @Override
    public Boolean deleteProduct(String productId) {
        ProductEntity productEntity = getProductOrThrow(productId);

        productEntity.setProductState(ProductState.DEACTIVATE);
        productRepository.save(productEntity);
        return true;
    }
}
