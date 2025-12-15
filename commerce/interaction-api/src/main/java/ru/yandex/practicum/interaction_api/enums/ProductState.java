package ru.yandex.practicum.interaction_api.enums;

import java.util.List;

public enum ProductState {
    ACTIVE(ProductState.StateType.productList.get(0)),
    DEACTIVATE(ProductState.StateType.productList.get(1));

    private final String state;

    ProductState(String state) {
        this.state = state;
    }

    public String getProduct() {
        return state;
    }

    public static boolean isCorrectProduct(String gn) {
        return ProductState.StateType.productList.contains(gn);
    }

    private static class StateType {
        public static final List<String> productList = List.of("ACTIVE", "DEACTIVATE");
    }
}
