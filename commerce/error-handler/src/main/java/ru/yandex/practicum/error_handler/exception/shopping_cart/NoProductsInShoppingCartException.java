package ru.yandex.practicum.error_handler.exception.shopping_cart;

public class NoProductsInShoppingCartException extends RuntimeException {
    public NoProductsInShoppingCartException(String message) {
        super(message);
    }
}
