package ru.yandex.practicum.shopping_cart.entity;

import org.mapstruct.*;
import ru.yandex.practicum.interaction_api.model.dto.ShoppingCartDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ShoppingCartMapper {

    @Mapping(target = "shoppingCartId", source = "id")
    @Mapping(target = "products", source = "cartProducts",
            qualifiedByName = "mapCartProductsToMap")
    ShoppingCartDto toDto(ShoppingCartEntity entity);

    @Named("mapCartProductsToMap")
    default Map<String, Integer> mapCartProductsToMap(List<CartProductEntity> cartProducts) {
        Map<String, Integer> result = new HashMap<>();
        if (cartProducts == null) return result;

        for (CartProductEntity cart : cartProducts) {
            if (cart == null || cart.getProductId() == null || cart.getQuantity() == null) continue;
            result.put(cart.getProductId(), cart.getQuantity());
        }
        return result;
    }

}