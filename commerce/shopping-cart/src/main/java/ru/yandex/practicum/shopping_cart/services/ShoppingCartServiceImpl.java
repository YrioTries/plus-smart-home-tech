package ru.yandex.practicum.shopping_cart.services;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.interaction_api.clients.WarehouseClient;
import ru.yandex.practicum.interaction_api.enums.ProductState;
import ru.yandex.practicum.interaction_api.enums.QuantityState;
import ru.yandex.practicum.interaction_api.enums.ShoppingCartState;
import ru.yandex.practicum.interaction_api.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.interaction_api.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.interaction_api.model.dto.ProductDto;
import ru.yandex.practicum.interaction_api.model.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.dto.request.ChangeProductQuantityRequest;
import ru.yandex.practicum.shopping_cart.entity.CartProductEntity;
import ru.yandex.practicum.shopping_cart.entity.ShoppingCartEntity;
import ru.yandex.practicum.shopping_cart.entity.ShoppingCartMapper;
import ru.yandex.practicum.shopping_cart.repositories.CartProductRepository;
import ru.yandex.practicum.shopping_cart.repositories.ShoppingCartRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final CartProductRepository cartProductRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final WarehouseClient warehouseClient;

    private ShoppingCartEntity getCartOrThrow(String username) {
        return shoppingCartRepository.findByOwner(username)
                .orElseThrow(() -> new NoProductsInShoppingCartException(
                        "Корзина не найдена для пользователя: " + username));
    }

    private void validateActive(ShoppingCartEntity cart) {
        if (cart.getState() != ShoppingCartState.ACTIVE) {
            throw new IllegalArgumentException("Корзина деактивирована");
        }
    }

    private ShoppingCartDto getCartDto(ShoppingCartEntity cart) {
        List<CartProductEntity> cartProductList =
                cartProductRepository.findByShoppingCart_Id(cart.getId());
        cart.setCartProducts(cartProductList);
        return shoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartDto getCurrentSoppingCart(String username) {
        // Корзина может быть пустой, но должна существовать
        ShoppingCartEntity shoppingCart = getCartOrThrow(username);
        return getCartDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(String username, List<String> productIds) {
        ShoppingCartEntity shoppingCart = getCartOrThrow(username);
        validateActive(shoppingCart);

        // Валидация: список не может быть пустым
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Список productIds не может быть пустым");
        }

        for (String productId : productIds) {
            cartProductRepository.deleteByShoppingCart_IdAndProductId(
                    shoppingCart.getId(), productId);
        }

        // Возвращаем обновленную корзину (может быть пустой - это нормально)
        return getCartDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        ShoppingCartEntity cart = getCartOrThrow(username);
        validateActive(cart);

        // Валидация запроса
        if (request.getProductId() == null || request.getProductId().isEmpty()) {
            throw new IllegalArgumentException("productId обязателен");
        }
        if (request.getNewQuantity() == null) {
            throw new IllegalArgumentException("newQuantity обязателен");
        }
        if (request.getNewQuantity() < 0) {
            throw new IllegalArgumentException("newQuantity не может быть отрицательным");
        }

        CartProductEntity item = cartProductRepository
                .findByShoppingCart_IdAndProductId(cart.getId(), request.getProductId())
                .orElseThrow(() -> new NoProductsInShoppingCartException(
                        "Товар с id " + request.getProductId() + " не найден в корзине"));

        if (request.getNewQuantity() == 0) {
            // Удаляем товар, если количество = 0
            cartProductRepository.delete(item);
        } else {
            item.setQuantity(request.getNewQuantity());
            cartProductRepository.save(item);
        }

        return getCartDto(cart);
    }

    @Override
    public ShoppingCartDto addInShoppingCart(String username, ProductDto product) {
        // Валидация ProductDto
        if (product == null) {
            throw new IllegalArgumentException("Product не может быть null");
        }
        if (product.getProductId() == null || product.getProductId().isEmpty()) {
            throw new IllegalArgumentException("ProductId обязателен");
        }

        // Проверка статуса товара ПЕРЕД добавлением
        if (product.getProductState() == ProductState.DEACTIVATE) {
            throw new IllegalArgumentException(
                    "Нельзя добавить деактивированный товар: " + product.getProductId());
        }
        if (product.getQuantityState() == QuantityState.ENDED) {
            throw new IllegalArgumentException(
                    "Товар закончился на складе: " + product.getProductId());
        }

        // Получаем или создаем корзину
        ShoppingCartEntity cart = shoppingCartRepository.findByOwner(username)
                .orElseGet(() -> {
                    ShoppingCartEntity newCart = new ShoppingCartEntity();
                    newCart.setId(UUID.randomUUID().toString());
                    newCart.setOwner(username);
                    newCart.setState(ShoppingCartState.ACTIVE);
                    return shoppingCartRepository.save(newCart);
                });

        validateActive(cart);

        String productId = product.getProductId();

        // Получаем или создаем CartProductEntity
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

        ShoppingCartDto cartDto = getCartDto(cart);

        // Проверка на складе (может бросить FeignException)
        try {
            warehouseClient.checkProductsWarehouse(cartDto);
        } catch (FeignException.NotFound ex) {
            // Откатываем добавление товара
            if (productItem.getQuantity() == 1) {
                cartProductRepository.delete(productItem);
            } else {
                productItem.setQuantity(productItem.getQuantity() - 1);
                cartProductRepository.save(productItem);
            }
            throw new NoSpecifiedProductInWarehouseException(
                    "Товар не найден на складе: " + productId);
        } catch (FeignException ex) {
            log.error("Ошибка при проверке склада: status={}, message={}",
                    ex.status(), ex.getMessage());
            // Откатываем добавление товара
            if (productItem.getQuantity() == 1) {
                cartProductRepository.delete(productItem);
            } else {
                productItem.setQuantity(productItem.getQuantity() - 1);
                cartProductRepository.save(productItem);
            }
            throw new IllegalArgumentException(
                    "Ошибка проверки товара на складе: " + ex.getMessage());
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
