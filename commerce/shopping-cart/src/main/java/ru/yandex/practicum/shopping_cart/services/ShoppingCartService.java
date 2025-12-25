package ru.yandex.practicum.shopping_cart.services;

import ru.yandex.practicum.interaction_api.model.dto.ProductDto;
import ru.yandex.practicum.interaction_api.model.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.dto.request.ChangeProductQuantityRequest;

import java.util.List;

public interface ShoppingCartService {

    ShoppingCartDto getCurrentSoppingCart(String username);

    ShoppingCartDto removeFromShoppingCart(String username, List<String> productIds);

    ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request);

    ShoppingCartDto addInShoppingCart(String username, ProductDto productDto);

    void deactivateShoppingCart(String username);
}
