package ru.yandex.practicum.shopping_cart.entity;

import org.mapstruct.*;
import ru.yandex.practicum.interaction_api.model.dto.ShoppingCartDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ShoppingCartMapper {

    @Mapping(target = "shoppingCartId", source = "shoppingCartId")
    @Mapping(target = "products", source = "cartProducts",
            qualifiedByName = "mapCartProductsToMap")
    ShoppingCartDto toDto(ShoppingCartEntity entity);

    @Named("mapCartProductsToMap")
    default Map<UUID, Integer> mapCartProductsToMap(List<CartProductEntity> cartProducts) {
        Map<UUID, Integer> result = new HashMap<>();
        if (cartProducts == null) return result;

        for (CartProductEntity cart : cartProducts) {
            if (cart == null || cart.getProductId() == null || cart.getQuantity() == null) continue;
            result.put(cart.getProductId(), cart.getQuantity());
        }
        return result;
    }
}