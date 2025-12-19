package ru.yandex.practicum.shopping_cart.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction_api.model.dto.ProductDto;
import ru.yandex.practicum.interaction_api.model.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.dto.request.ChangeProductQuantityRequest;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/shopping-cart")
public class ShoppingCartController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto getCurrentSoppingCart(String username) {
        return new ShoppingCartDto();
    }

    @PostMapping("/remove")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto removeFromShoppingCart(String username, @RequestBody List<String> productIds) {
        return new ShoppingCartDto();
    }

    @PostMapping("/change-quantity")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto changeProductQuantity(String username, @RequestBody ChangeProductQuantityRequest request) {
        return new ShoppingCartDto();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto addInShoppingCart(String username, @RequestBody List<ProductDto> productList) {
        return new ShoppingCartDto();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deactivateShoppingCart(String username) {
    }
}
