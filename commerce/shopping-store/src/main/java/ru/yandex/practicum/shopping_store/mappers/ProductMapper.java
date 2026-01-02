package ru.yandex.practicum.shopping_store.mappers;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.interaction_api.model.enums.ProductCategory;
import ru.yandex.practicum.interaction_api.model.enums.ProductState;
import ru.yandex.practicum.interaction_api.model.enums.QuantityState;
import ru.yandex.practicum.interaction_api.model.dto.shopping_store.ProductDto;
import ru.yandex.practicum.shopping_store.entity.ProductDao;

@UtilityClass
public class ProductMapper {

    public static ProductDto toDto(ProductDao productDao) {
        if (productDao == null) return null;

        return ProductDto.builder()
                .productId(productDao.getProductId())
                .productName(productDao.getProductName())
                .description(productDao.getDescription())
                .imageSrc(productDao.getImageSrc())
                .quantityState(QuantityState.valueOf(
                        productDao.getQuantityState().name()))
                .productState(ProductState.valueOf(
                        productDao.getProductState().name()))
                .productCategory(ProductCategory.valueOf(
                        productDao.getProductCategory().name()))
                .price(productDao.getPrice())
                .build();
    }

    public static ProductDao toEntity(ProductDto dto) {
        if (dto == null) return null;

        return ProductDao.builder()
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

    public static ProductDao updateFields(ProductDao oldProductDao, ProductDto newData) {
        ProductDao updatedProductDao = new ProductDao();

        updatedProductDao.setProductId(oldProductDao.getProductId());
        updatedProductDao.setDescription(newData.getDescription() != null ? newData.getDescription() : oldProductDao.getDescription());
        updatedProductDao.setImageSrc(newData.getImageSrc() != null ? newData.getImageSrc() : oldProductDao.getImageSrc());
        updatedProductDao.setProductName(newData.getProductName() != null ? newData.getProductName() : oldProductDao.getProductName());
        updatedProductDao.setPrice(newData.getPrice() != null ? newData.getPrice() : oldProductDao.getPrice());
        updatedProductDao.setProductCategory(newData.getProductCategory() != null
                ? ProductCategory.valueOf(newData.getProductCategory().name())
                : oldProductDao.getProductCategory());
        updatedProductDao.setQuantityState(newData.getQuantityState() != null
                ? QuantityState.valueOf(newData.getQuantityState().name())
                : oldProductDao.getQuantityState());
        updatedProductDao.setProductState(newData.getProductState() != null
                ? ProductState.valueOf(newData.getProductState().name())
                : oldProductDao.getProductState());

        return updatedProductDao;
    }
}