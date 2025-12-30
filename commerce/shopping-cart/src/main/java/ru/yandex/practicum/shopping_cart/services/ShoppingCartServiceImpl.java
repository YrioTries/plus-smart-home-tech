package ru.yandex.practicum.shopping_cart.services;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.error_handler.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.error_handler.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.error_handler.exception.NotAuthorizedUserException;
import ru.yandex.practicum.interaction_api.clients.WarehouseClient;
import ru.yandex.practicum.interaction_api.enums.ShoppingCartState;
import ru.yandex.practicum.interaction_api.model.dto.ProductDto;
import ru.yandex.practicum.interaction_api.model.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.dto.request.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction_api.model.dto.request.RemoveProductsRequest;
import ru.yandex.practicum.shopping_cart.entity.CartProductEntity;
import ru.yandex.practicum.shopping_cart.entity.ShoppingCartEntity;
import ru.yandex.practicum.shopping_cart.entity.ShoppingCartMapper;
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

    private final WarehouseClient warehouseClient;

    private ShoppingCartEntity getCartOrThrow(String username) {
        if (username == null) {
            throw new NotAuthorizedUserException("Имя пользователя не может быть пустым!");
        }

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
        List<CartProductEntity> cartProductList = cartProductRepository.findByCartId(shoppingCart.getShoppingCartId());
        shoppingCart.setCartProducts(cartProductList);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(String username, RemoveProductsRequest request) {
        ShoppingCartEntity shoppingCart = getCartOrThrow(username);
        validateActive(shoppingCart);

        for (UUID productId : request.getProductIds()) {
            cartProductRepository.deleteByCartIdAndProductId(shoppingCart.getShoppingCartId(), productId);
        }

        return getCurrentSoppingCart(username);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        ShoppingCartEntity cart = getCartOrThrow(username);
        validateActive(cart);

        CartProductEntity carProductEntity = cartProductRepository
                .findByCartIdAndProductId(cart.getShoppingCartId(), request.getProductId())
                .orElseThrow(() -> new NoProductsInShoppingCartException("Товар не найден в корзине"));

        carProductEntity.setQuantity(request.getNewQuantity());
        if (carProductEntity.getQuantity() <= 0) {
            cartProductRepository.delete(carProductEntity);
        } else {
            cartProductRepository.save(carProductEntity);
        }

        return getCurrentSoppingCart(username);
    }

    @Override
    public ShoppingCartDto addInShoppingCart(String username, ProductDto product) {
        if (username == null) {
            throw new NotAuthorizedUserException("Имя пользователя не может быть пустым!");
        }

        final UUID productId = product.productId();
        ShoppingCartDto cartDto;

        try {
            ShoppingCartEntity cart = shoppingCartRepository.findByOwner(username)
                    .orElseGet(() -> {
                        ShoppingCartEntity newCart = new ShoppingCartEntity();
                        newCart.setOwner(username);
                        newCart.setState(ShoppingCartState.ACTIVE);
                        return newCart;
                    });

            // чтобы сгенерировать id
            ShoppingCartEntity shoppingCartEntity = shoppingCartRepository.save(cart);
            validateActive(cart);

            CartProductEntity productItem = cartProductRepository
                    .findByCartIdAndProductId(cart.getShoppingCartId(), productId)
                    .orElseGet(() -> {
                        CartProductEntity newCartProductEntity = new CartProductEntity();
                        newCartProductEntity.setCartId(shoppingCartEntity.getShoppingCartId());
                        newCartProductEntity.setProductId(productId);
                        newCartProductEntity.setQuantity(0);
                        newCartProductEntity.setShoppingCart(shoppingCartEntity);
                        return newCartProductEntity;
                    });

            productItem.setQuantity(productItem.getQuantity() + 1);
            cartProductRepository.save(productItem);

            cartDto = shoppingCartMapper.toDto(shoppingCartEntity);
            warehouseClient.checkProductsWarehouse(cartDto);

        } catch (FeignException.NotFound ex) {
            throw new NoSpecifiedProductInWarehouseException(
                    "Товар не найден на складе: " + productId
            );
        }
        return cartDto;
    }

    @Override
    public void deactivateShoppingCart(String username) {
        ShoppingCartEntity cart = getCartOrThrow(username);
        cart.setState(ShoppingCartState.DEACTIVATE);
        shoppingCartRepository.save(cart);
    }
}

