package ru.yandex.practicum.warehouse.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.ProductInWarehouseDto;
import ru.yandex.practicum.warehouse.entity.ProductInWarehouseDao;

@UtilityClass
public class ProductInWarehouseMapper {

    public static ProductInWarehouseDto toDto(ProductInWarehouseDao entity) {
        return ProductInWarehouseDto.builder()
                .productId(entity.getProductId())
                .fragile(entity.getFragile())
                .dimension(entity.getDimension())
                .weight(entity.getWeight())
                .build();
    }

    public static ProductInWarehouseDao toEntity(NewProductInWarehouseRequest newProduct) {
        return ProductInWarehouseDao.builder()
                .productId(newProduct.getProductId())
                .fragile(newProduct.getFragile())
                .dimension(newProduct.getDimension())
                .weight(newProduct.getWeight())
                .build();
    }
}
