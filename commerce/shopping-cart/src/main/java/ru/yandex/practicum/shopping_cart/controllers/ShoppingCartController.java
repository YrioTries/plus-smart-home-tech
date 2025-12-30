package ru.yandex.practicum.shopping_cart.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction_api.model.dto.ProductDto;
import ru.yandex.practicum.interaction_api.model.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.dto.request.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction_api.model.dto.request.RemoveProductsRequest;
import ru.yandex.practicum.shopping_cart.services.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/shopping-cart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto getCurrentSoppingCart(@RequestParam String username) {
        return shoppingCartService.getCurrentSoppingCart(username);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto addInShoppingCart(@RequestParam String username, @RequestBody Map<UUID, Integer> products) {
        return shoppingCartService.addInShoppingCart(username, products);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateShoppingCart(@RequestParam String username) {
        shoppingCartService.deactivateShoppingCart(username);
    }

    @PostMapping("/remove")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto removeFromShoppingCart(@RequestParam String username, @RequestBody List<UUID> products) {
        return shoppingCartService.removeFromShoppingCart(username, products);
    }

    @PostMapping("/change-quantity")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto changeProductQuantity(@RequestParam String username, @RequestBody ChangeProductQuantityRequest request) {
        return shoppingCartService.changeProductQuantity(username, request);
    }
}
