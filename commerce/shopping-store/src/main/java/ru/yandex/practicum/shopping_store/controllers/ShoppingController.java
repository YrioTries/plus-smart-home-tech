package ru.yandex.practicum.shopping_store.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction_api.dto.Pageable;

import java.util.ArrayList;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/shopping-store")
public class ShoppingController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public void getPageableListOfProducts(
            @RequestParam(name = "category") String category,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", required = false) ArrayList<String> sort) {

        Pageable pageable = new Pageable(page, size, sort);
    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public void getProductInfo(@PathVariable Long productId) {
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct() {
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void updateProduct() {
    }

    @PostMapping("/quantityState")
    @ResponseStatus(HttpStatus.OK)
    public void setProductStatus() {
    }

    @PostMapping("/removeProductFromStore")
    @ResponseStatus(HttpStatus.OK)
    public void deleteProduct() {
    }
}
