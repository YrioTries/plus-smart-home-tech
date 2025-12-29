package ru.yandex.practicum.shopping_store.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.interaction_api.enums.ProductCategory;
import ru.yandex.practicum.interaction_api.enums.ProductState;
import ru.yandex.practicum.interaction_api.enums.QuantityState;
import ru.yandex.practicum.interaction_api.exception.ProductNotFoundException;
import ru.yandex.practicum.interaction_api.model.dto.ProductDto;
import ru.yandex.practicum.shopping_store.entity.ProductEntity;
import ru.yandex.practicum.shopping_store.entity.ProductMapper;
import ru.yandex.practicum.shopping_store.repositories.ProductRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShoppingStoreServiceImpl implements ShoppingStoreService{

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    private ProductEntity getProductOrThrow(UUID productId) {
        return productRepository
                .findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Товар с id:" + productId +"не найден"));
    }

    @Override
    public Page<ProductDto> getPageableListOfProducts(Pageable pageable, ProductCategory category) {
        Page<ProductEntity> entityPage = productRepository.findByProductCategory(category, pageable);

        if (entityPage.isEmpty()) {
            throw new ProductNotFoundException("No products found for category: " + category);
        }

        List<ProductDto> productDtos = entityPage.getContent().stream()
                .filter(p -> p.getProductState() == ProductState.ACTIVE)
                .map(productMapper::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(productDtos);
    }


    @Override
    public ProductDto getProductInfo(UUID productId) {
        ProductEntity productEntity = getProductOrThrow(productId);
        return productMapper.toDto(productEntity);
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {

        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductName(productDto.getProductName());
        productEntity.setDescription(productDto.getDescription());
        productEntity.setImageSrc(productDto.getImageSrc());
        productEntity.setQuantityState(productDto.getQuantityState());
        productEntity.setProductState(
                productDto.getProductState() != null ?
                        productDto.getProductState() :
                        ProductState.ACTIVE
        );
        productEntity.setProductCategory(productDto.getProductCategory());
        productEntity.setPrice(productDto.getPrice());


        if (productEntity.getProductState() == null) {
            productEntity.setProductState(ProductState.ACTIVE);
        }

        productRepository.save(productEntity);

        return productMapper.toDto(productEntity);
    }


    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        ProductEntity productEntity = getProductOrThrow(productDto.getProductId());

        productEntity.setProductName(productDto.getProductName());
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
    public Boolean setProductQuantityState(UUID productId, QuantityState quantityState) {
        ProductEntity productEntity = getProductOrThrow(productId);

        productEntity.setQuantityState(quantityState);
        productRepository.save(productEntity);
        return true;
    }

    @Override
    public Boolean deleteProduct(UUID productId) {
        ProductEntity productEntity = getProductOrThrow(productId);

        productEntity.setProductState(ProductState.DEACTIVATE);
        productRepository.save(productEntity);
        return true;
    }
}
