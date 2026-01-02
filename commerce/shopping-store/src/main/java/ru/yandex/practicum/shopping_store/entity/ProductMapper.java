package ru.yandex.practicum.shopping_store.entity;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.interaction_api.model.enums.ProductCategory;
import ru.yandex.practicum.interaction_api.model.enums.ProductState;
import ru.yandex.practicum.interaction_api.model.enums.QuantityState;
import ru.yandex.practicum.interaction_api.model.dto.shopping_store.ProductDto;

@UtilityClass
public class ProductMapper {

    public static ProductDto toDto(Product product) {
        if (product == null) return null;

        return ProductDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .imageSrc(product.getImageSrc())
                .quantityState(QuantityState.valueOf(
                        product.getQuantityState().name()))
                .productState(ProductState.valueOf(
                        product.getProductState().name()))
                .productCategory(ProductCategory.valueOf(
                        product.getProductCategory().name()))
                .price(product.getPrice())
                .build();
    }

    public static Product toEntity(ProductDto dto) {
        if (dto == null) return null;

        return Product.builder()
                .productId(dto.getProductId())
                .productName(dto.getProductName())
                .description(dto.getDescription())
                .imageSrc(dto.getImageSrc())
                .quantityState(QuantityState.valueOf(
                        dto.getQuantityState().name()))
                .productState(ProductState.valueOf(
                        dto.getProductState().name()))
                .productCategory(ProductCategory.valueOf(
                        dto.getProductCategory().name()))
                .price(dto.getPrice())
                .build();
    }

    public static Product updateFields(Product oldProduct, ProductDto newData) {
        Product updatedProduct = new Product();

        updatedProduct.setProductId(oldProduct.getProductId());
        updatedProduct.setDescription(newData.getDescription() != null ? newData.getDescription() : oldProduct.getDescription());
        updatedProduct.setImageSrc(newData.getImageSrc() != null ? newData.getImageSrc() : oldProduct.getImageSrc());
        updatedProduct.setProductName(newData.getProductName() != null ? newData.getProductName() : oldProduct.getProductName());
        updatedProduct.setPrice(newData.getPrice() != null ? newData.getPrice() : oldProduct.getPrice());
        updatedProduct.setProductCategory(newData.getProductCategory() != null
                ? ProductCategory.valueOf(newData.getProductCategory().name())
                : oldProduct.getProductCategory());
        updatedProduct.setQuantityState(newData.getQuantityState() != null
                ? QuantityState.valueOf(newData.getQuantityState().name())
                : oldProduct.getQuantityState());
        updatedProduct.setProductState(newData.getProductState() != null
                ? ProductState.valueOf(newData.getProductState().name())
                : oldProduct.getProductState());

        return updatedProduct;
    }
}