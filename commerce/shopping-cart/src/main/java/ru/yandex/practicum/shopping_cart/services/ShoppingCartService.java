package ru.yandex.practicum.shopping_cart.services;

import ru.yandex.practicum.interaction_api.model.dto.ProductDto;
import ru.yandex.practicum.interaction_api.model.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.dto.request.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction_api.model.dto.request.RemoveProductsRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {

    ShoppingCartDto getCurrentSoppingCart(String username);

    ShoppingCartDto removeFromShoppingCart(String username, RemoveProductsRequest request);

    ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request);

    ShoppingCartDto addInShoppingCart(String username, Map<UUID, Integer> products);

    void deactivateShoppingCart(String username);
}
