package ru.yandex.practicum.shopping_cart.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.interaction_api.enums.ShoppingCartState;
import ru.yandex.practicum.interaction_api.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.interaction_api.model.dto.ProductDto;
import ru.yandex.practicum.interaction_api.model.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.dto.request.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction_api.model.entity.CartProductEntity;
import ru.yandex.practicum.interaction_api.model.entity.ProductEntity;
import ru.yandex.practicum.interaction_api.model.entity.ShoppingCartEntity;
import ru.yandex.practicum.interaction_api.model.mappers.ShoppingCartMapper;
import ru.yandex.practicum.shopping_cart.repositories.CartProductRepository;
import ru.yandex.practicum.shopping_cart.repositories.ShoppingCartRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final CartProductRepository cartProductRepository;

    private final ShoppingCartMapper shoppingCartMapper;

    private ShoppingCartEntity getCartOrThrow(String username) {
        return shoppingCartRepository.findByOwner(username)
                .orElseThrow(() -> new NoProductsInShoppingCartException("Корзина не найдена для пользователя: " + username));
    }

    private void validateActive(ShoppingCartEntity cart) {
        if (cart.getState() != ShoppingCartState.ACTIVE) {
            throw new NoProductsInShoppingCartException("Корзина деактивирована");
        }
    }

    @Override
    public ShoppingCartDto getCurrentSoppingCart(String username) {
        ShoppingCartEntity shoppingCart = getCartOrThrow(username);
        List<CartProductEntity> cartProductList = cartProductRepository.findByCartId(shoppingCart.getId());
        shoppingCart.setCartProducts(cartProductList);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(String username, List<String> productIds) {
        ShoppingCartEntity shoppingCart = getCartOrThrow(username);
        validateActive(shoppingCart);

        for (String productId : productIds) {
            cartProductRepository.deleteByShoppingCart_IdAndProductId(shoppingCart.getId(), productId);
        }

        return getCurrentSoppingCart(username);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        ShoppingCartEntity cart = getCartOrThrow(username);
        validateActive(cart);

        CartProductEntity item = cartProductRepository
                .findByShoppingCart_IdAndProductId(cart.getId(), request.getProductId())
                .orElseThrow(() -> new RuntimeException("Товар не найден в корзине"));

        item.setQuantity(request.getNewQuantity()); // или плюс/минус, как в схеме
        if (item.getQuantity() <= 0) {
            cartProductRepository.delete(item);
        } else {
            cartProductRepository.save(item);
        }

        return getCurrentSoppingCart(username);
    }

    @Override
    public ShoppingCartDto addInShoppingCart(String username, List<ProductDto> productList) {
        ShoppingCartEntity cart = shoppingCartRepository.findByOwner(username)
                .orElseGet(() -> {
                    ShoppingCartEntity newCart = new ShoppingCartEntity();
                    newCart.setId(UUID.randomUUID().toString());
                    newCart.setOwner(username);
                    newCart.setState(ShoppingCartState.ACTIVE);
                    return shoppingCartRepository.save(newCart);
                });

        validateActive(cart);

        for (ProductDto dto : productList) {
            String productId = dto.getProductId();

            CartProductEntity productItem = cartProductRepository
                    .findByShoppingCart_IdAndProductId(cart.getId(), productId)
                    .orElseGet(() -> {
                        CartProductEntity newItem = new CartProductEntity();
                        newItem.setShoppingCart(cart);
                        newItem.setProductId(productId);
                        newItem.setQuantity(0);
                        return newItem;
                    });

            productItem.setQuantity(productItem.getQuantity() + 1);
            cartProductRepository.save(productItem);
        }

        return getCurrentSoppingCart(username);
    }

    @Override
    public void deactivateShoppingCart(String username) {
        ShoppingCartEntity cart = getCartOrThrow(username);
        cart.setState(ShoppingCartState.DEACTIVATE);
        shoppingCartRepository.save(cart);
    }
}

