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
import ru.yandex.practicum.shopping_cart.entity.ShoppingCartDao;
import ru.yandex.practicum.shopping_cart.mappers.ShoppingCartMapper;
import ru.yandex.practicum.shopping_cart.repositories.ShoppingCartRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final WarehouseClient warehouseClient;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        validateUserAuthorize(username);
        return ShoppingCartMapper.toDto(cartExistsByUsername(username));
    }

    @Override
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Integer> products) {
        validateUserAuthorize(username);

        try {
            ShoppingCartDao shoppingCartDao = cartExistsByUsername(username);

            warehouseClient.checkQuantityForCart(ShoppingCartMapper.toDto(shoppingCartDao));
            ShoppingCartDao updated = addProductsToShoppingCart(shoppingCartDao, products);

            return ShoppingCartMapper.toDto(shoppingCartRepository.save(updated));
        }
        catch (CartNotFoundException e) {
            ShoppingCartDao newShoppingCart = ShoppingCartDao.builder()
                    .items(new ArrayList<>())
                    .owner(username)
                    .build();

            ShoppingCartDao updated = addProductsToShoppingCart(newShoppingCart, products);

            return ShoppingCartMapper.toDto(shoppingCartRepository.save(updated));
        }
    }

    @Override
    public void deactivateShoppingCart(String username) {
        validateUserAuthorize(username);

        ShoppingCartDao shoppingCart = cartExistsByUsername(username);
        shoppingCart.setState(ShoppingCartState.DEACTIVATED);

        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartDto removeProductFromShoppingCart(String username, List<UUID> products) {
        validateUserAuthorize(username);

        ShoppingCartDao shoppingCart = cartExistsByUsername(username);
        shoppingCart.getItems().removeIf(item -> products.contains(item.getProductId()));

        return ShoppingCartMapper.toDto(shoppingCartRepository.save(shoppingCart));
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        validateUserAuthorize(username);

        ShoppingCartDao shoppingCart = cartExistsByUsername(username);

        for (ShoppingCartItem item : shoppingCart.getItems()) {
            if (item.getProductId().equals(request.getProductId())) {
                item.setQuantity(request.getNewQuantity());
                break;
            }
        }
        warehouseClient.checkQuantityForCart(ShoppingCartMapper.toDto(shoppingCart));

        return ShoppingCartMapper.toDto(shoppingCartRepository.save(shoppingCart));
    }

    private ShoppingCartDao cartExistsByUsername(String username) {
        ShoppingCartDao shoppingCart = shoppingCartRepository.findByOwner(username)
                .orElseThrow(() -> new CartNotFoundException("Корзина для пользователя " + username + " не найдена!"));

        if (shoppingCart.getState().equals(ShoppingCartState.DEACTIVATED)) {
            throw new DeactivatedCartException("Корзина была диактивирована!");
        }

        return shoppingCart;
    }

    private ShoppingCartDao addProductsToShoppingCart(ShoppingCartDao shoppingCart, Map<UUID, Integer> products) {

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

    private void validateUserAuthorize(String username) {
        if (username == null) {
            throw new NotAuthorizedUserException("Имя пользователя не может быть пустым!");
        }
    }
}

