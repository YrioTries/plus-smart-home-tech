package ru.yandex.practicum.interaction_api.enums;

import java.util.List;

public enum ProductCategory {
    CONTROL(ProductType.productList.get(0)),
    SENSORS(ProductType.productList.get(1)),
    LIGHTING(ProductType.productList.get(2));

    private final String product;

    ProductCategory(String product) {
        this.product = product;
    }

    public String getProduct() {
        return product;
    }

    public static boolean isCorrectProduct(String gn) {
        return ProductType.productList.contains(gn);
    }

    private static class ProductType {
        public static final List<String> productList = List.of("CONTROL", "SENSORS", "LIGHTING");
    }
}
