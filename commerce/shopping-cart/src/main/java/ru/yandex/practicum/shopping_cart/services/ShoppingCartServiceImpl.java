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

    private final ShoppingCartRepository shoppingCartRepository;
    private final CartProductRepository cartProductRepository;
    private final ProductRepository productRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    public ShoppingCartDto getCurrentSoppingCart(String username) {
        // Находим корзину пользователя
        ShoppingCartEntity cart = shoppingCartRepository.findByOwner(username)
                .orElseThrow(() -> new RuntimeException("Корзина не найдена для пользователя: " + username));

        // Загружаем товары в корзине (если lazy loading)
        List<CartProductEntity> cartItems = cartProductRepository.findByCartId(cart.getId());
        cart.setCartProducts(cartItems);

        // Используем маппер для преобразования в DTO
        return shoppingCartMapper.toDto(cart);
    }

    public ShoppingCartDto removeFromShoppingCart(String username, List<String> productIds) {
        // Находим корзину пользователя
        ShoppingCartEntity cart = shoppingCartRepository.findByOwner(username)
                .orElseThrow(() -> new RuntimeException("Корзина не найдена для пользователя: " + username));

        // Проверяем, что корзина активна
        validateCartState(cart);

        // Удаляем каждый указанный товар из корзины
        for (String productId : productIds) {
            cartProductRepository.deleteByCartIdAndProductId(cart.getId(), productId);
        }

        // Получаем обновленное состояние корзины
        return getCurrentSoppingCart(username);
    }

    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        return new ShoppingCartDto();
    }

    public ShoppingCartDto addInShoppingCart(String username, List<ProductDto> productList) {
        ShoppingCartEntity cart = shoppingCartRepository.findByOwner(username)
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
