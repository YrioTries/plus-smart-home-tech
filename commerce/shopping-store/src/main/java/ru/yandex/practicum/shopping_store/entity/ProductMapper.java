package ru.yandex.practicum.shopping_store.entity;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.interaction_api.model.dto.ProductDto;
import ru.yandex.practicum.interaction_api.model.entity.ProductEntity;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProductMapper {

    @Mapping(target = "productId", source = "id")
    @Mapping(target = "productName", source = "name")
    ProductDto toDto(ProductEntity entity);

    @Mapping(target = "id", source = "productId")
    @Mapping(target = "name", source = "productName")
    ProductEntity toEntity(ProductDto dto);
}