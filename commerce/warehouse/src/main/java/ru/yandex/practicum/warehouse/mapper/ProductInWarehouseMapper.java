package ru.yandex.practicum.warehouse.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.ProductInWarehouseDto;
import ru.yandex.practicum.warehouse.entity.ProductInWarehouse;

@UtilityClass
public class ProductInWarehouseMapper {

    public static ProductInWarehouseDto toDto(ProductInWarehouse entity) {
        return ProductInWarehouseDto.builder()
                .productId(entity.getProductId())
                .fragile(entity.getFragile())
                .dimension(entity.getDimension())
                .weight(entity.getWeight())
                .build();
    }

    public static ProductInWarehouse toEntity(NewProductInWarehouseRequest newProduct) {
        return ProductInWarehouse.builder()
                .productId(newProduct.getProductId())
                .fragile(newProduct.getFragile())
                .dimension(newProduct.getDimension())
                .weight(newProduct.getWeight())
                .build();
    }
}
