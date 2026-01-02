package ru.yandex.practicum.shopping_cart.mappers;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.interaction_api.model.dto.shopping_cart.ShoppingCartDto;
import ru.yandex.practicum.shopping_cart.entity.ShoppingCartEntity;
import ru.yandex.practicum.shopping_cart.entity.ShoppingCartItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@UtilityClass
public class ShoppingCartMapper {

    public static ShoppingCartDto toDto(ShoppingCartEntity shoppingCart) {
        return ShoppingCartDto.builder()
                .shoppingCartId(shoppingCart.getShoppingCartId())
                .products(getProductsMap(shoppingCart.getItems()))
                .build();
    }

    private Map<UUID, Integer> getProductsMap(List<ShoppingCartItem> products) {
        Map<UUID, Integer> productsMap = new HashMap<>();

        products.forEach(product -> productsMap.put(product.getProductId(), product.getQuantity()));
        return productsMap;
    }

    private List<ShoppingCartItem> getProductsList(Map<UUID, Integer> products) {
        return products.entrySet().stream()
                .map(entry -> {
                    ShoppingCartItem item = new ShoppingCartItem();
                    item.setProductId(entry.getKey());
                    item.setQuantity(entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }
}