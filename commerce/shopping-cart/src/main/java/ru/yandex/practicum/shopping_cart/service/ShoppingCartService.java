package ru.yandex.practicum.shopping_cart.service;

import ru.yandex.practicum.interaction_api.model.shopping_cart.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.shopping_cart.dto.request.ChangeProductQuantityRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {

    ShoppingCartDto getShoppingCart(String username);

    ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Integer> products);

    void deactivateShoppingCart(String username);

    ShoppingCartDto removeProductFromShoppingCart(String username, List<UUID> products);

    ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request);
}
