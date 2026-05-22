package ru.yandex.practicum.shopping_cart.model.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.interaction_api.model.shopping_cart.dto.ShoppingCartDto;
import ru.yandex.practicum.shopping_cart.model.entity.ShoppingCartDao;
import ru.yandex.practicum.shopping_cart.model.entity.ShoppingCartItemDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@UtilityClass
public class ShoppingCartMapper {

    public static ShoppingCartDto toDto(ShoppingCartDao shoppingCart) {
        return ShoppingCartDto.builder()
                .shoppingCartId(shoppingCart.getShoppingCartId())
                .products(getProductsMap(shoppingCart.getItems()))
                .build();
    }

    private Map<UUID, Integer> getProductsMap(List<ShoppingCartItemDao> products) {
        Map<UUID, Integer> productsMap = new HashMap<>();

        products.forEach(product -> productsMap.put(product.getProductId(), product.getQuantity()));
        return productsMap;
    }

    private List<ShoppingCartItemDao> getProductsList(Map<UUID, Integer> products) {
        return products.entrySet().stream()
                .map(entry -> {
                    ShoppingCartItemDao item = new ShoppingCartItemDao();
                    item.setProductId(entry.getKey());
                    item.setQuantity(entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }
}