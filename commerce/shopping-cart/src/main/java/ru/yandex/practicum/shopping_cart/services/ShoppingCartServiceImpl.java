package ru.yandex.practicum.shopping_cart.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.error_handler.exception.CartNotFoundException;
import ru.yandex.practicum.error_handler.exception.DeactivatedCartException;
import ru.yandex.practicum.error_handler.exception.NotAuthorizedUserException;
import ru.yandex.practicum.interaction_api.clients.WarehouseClient;
import ru.yandex.practicum.interaction_api.model.enums.ShoppingCartState;
import ru.yandex.practicum.interaction_api.model.dto.shopping_cart.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.dto.shopping_cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.shopping_cart.entity.ShoppingCartItem;
import ru.yandex.practicum.shopping_cart.entity.ShoppingCartEntity;
import ru.yandex.practicum.shopping_cart.mappers.ShoppingCartMapper;
import ru.yandex.practicum.shopping_cart.repositories.ShoppingCartRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository repository;
    private final WarehouseClient client;

    @Override
    public ShoppingCartDto getCart(String username) {
        if (username == null) {
            throw new NotAuthorizedUserException("Имя пользователя не может быть пустым!");
        }

        return ShoppingCartMapper.toDto(cartExistsByUsername(username));
    }

    @Override
    public ShoppingCartDto addProductToCart(String username, Map<UUID, Integer> products) {
        if (username == null) {
            throw new NotAuthorizedUserException("Имя пользователя не может быть пустым!");
        }

        try {
            ShoppingCartEntity shoppingCart = cartExistsByUsername(username);

            client.checkQuantityForCart(ShoppingCartMapper.toDto(shoppingCart));
            ShoppingCartEntity updated = addProductsToCart(shoppingCart, products);

            return ShoppingCartMapper.toDto(repository.save(updated));
        }
        catch (CartNotFoundException e) {
            ShoppingCartEntity newShoppingCart = ShoppingCartEntity.builder()
                    .items(new ArrayList<>())
                    .owner(username)
                    .build();

            ShoppingCartEntity updated = addProductsToCart(newShoppingCart, products);

            return ShoppingCartMapper.toDto(repository.save(updated));
        }
    }

    @Override
    public void deactivateCart(String username) {
        if (username == null) {
            throw new NotAuthorizedUserException("Имя пользователя не может быть пустым!");
        }

        ShoppingCartEntity shoppingCart = cartExistsByUsername(username);
        shoppingCart.setState(ShoppingCartState.DEACTIVATED);

        repository.save(shoppingCart);
    }

    @Override
    public ShoppingCartDto removeProductFromCart(String username, List<UUID> products) {
        if (username == null) {
            throw new NotAuthorizedUserException("Имя пользователя не может быть пустым!");
        }

        ShoppingCartEntity shoppingCart = cartExistsByUsername(username);
        shoppingCart.getItems().removeIf(item -> products.contains(item.getProductId()));

        return ShoppingCartMapper.toDto(repository.save(shoppingCart));
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        if (username == null) {
            throw new NotAuthorizedUserException("Имя пользователя не может быть пустым!");
        }

        ShoppingCartEntity shoppingCart = cartExistsByUsername(username);

        for (ShoppingCartItem item : shoppingCart.getItems()) {
            if (item.getProductId().equals(request.getProductId())) {
                item.setQuantity(request.getNewQuantity());
                break;
            }
        }
        client.checkQuantityForCart(ShoppingCartMapper.toDto(shoppingCart));

        return ShoppingCartMapper.toDto(repository.save(shoppingCart));
    }

    private ShoppingCartEntity cartExistsByUsername(String username) {
        ShoppingCartEntity shoppingCart = repository.findByOwner(username)
                .orElseThrow(() -> new CartNotFoundException("Корзина для пользователя " + username + " не найдена!"));

        if (shoppingCart.getState().equals(ShoppingCartState.DEACTIVATED)) {
            throw new DeactivatedCartException("Корзина была диактивирована!");
        }

        return shoppingCart;
    }

    private ShoppingCartEntity addProductsToCart(ShoppingCartEntity shoppingCart, Map<UUID, Integer> products) {

        Map<UUID, Integer> validProducts = new HashMap<>(products);
        Map<UUID, ShoppingCartItem> itemMap = shoppingCart.getItems().stream()
                .collect(Collectors.toMap(ShoppingCartItem::getProductId, Function.identity()));

        validProducts.forEach((productId, quantity) -> {
            if (itemMap.containsKey(productId)) {
                ShoppingCartItem existingItem = itemMap.get(productId);
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
            } else {
                ShoppingCartItem newItem = ShoppingCartItem.builder()
                        .shoppingCart(shoppingCart)
                        .productId(productId)
                        .quantity(quantity)
                        .build();
                shoppingCart.getItems().add(newItem);
            }
        });

        return shoppingCart;
    }
}

