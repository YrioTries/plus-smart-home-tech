package ru.yandex.practicum.shopping_cart.services;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.interaction_api.model.dto.ProductDto;
import ru.yandex.practicum.interaction_api.model.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.dto.request.ChangeProductQuantityRequest;

import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService{

    public ShoppingCartDto getCurrentSoppingCart(String username) {
        return new ShoppingCartDto();
    }

    public ShoppingCartDto removeFromShoppingCart(String username, List<String> productIds) {
        return new ShoppingCartDto();
    }

    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        return new ShoppingCartDto();
    }

    public ShoppingCartDto addInShoppingCart(String username, List<ProductDto> productList) {
        return new ShoppingCartDto();
    }

    public void deactivateShoppingCart(String username) {
    }
}
