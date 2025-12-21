package ru.yandex.practicum.shopping_cart.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.interaction_api.enums.ShoppingCartState;
import ru.yandex.practicum.interaction_api.model.dto.ProductDto;
import ru.yandex.practicum.interaction_api.model.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.dto.request.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction_api.model.entity.ShoppingCartEntity;
import ru.yandex.practicum.shopping_cart.repositories.ShoppingCartRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService{

    private final ShoppingCartRepository repository;

    public ShoppingCartDto getCurrentSoppingCart(String username) {
        ShoppingCartDto cartDto = repository.findByOwner(username).orElseGet()
        return new ShoppingCartDto();
    }

    public ShoppingCartDto removeFromShoppingCart(String username, List<String> productIds) {
        return new ShoppingCartDto();
    }

    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        return new ShoppingCartDto();
    }

    public ShoppingCartDto addInShoppingCart(String username, List<ProductDto> productList) {
        ShoppingCartEntity cart = repository.findByOwner(username)
                .orElseGet(() -> {
                    ShoppingCartEntity newCart = new ShoppingCartEntity();
                    newCart.setId(UUID.randomUUID().toString());
                    newCart.setOwner(username);
                    newCart.setState(ShoppingCartState.ACTIVE);
                    return newCart;
                });

        return new ShoppingCartDto();
    }

    public void deactivateShoppingCart(String username) {
    }
}
