package ru.yandex.practicum.interaction_api.enums;

import java.util.List;

public enum ShoppingCartState {
    ACTIVE(ShoppingCartState.StateType.productList.get(0)),
    DEACTIVATE(ShoppingCartState.StateType.productList.get(1));

    private final String state;

    ShoppingCartState(String state) {
        this.state = state;
    }

    public String getProduct() {
        return state;
    }

    public static boolean isCorrectProduct(String gn) {
        return ShoppingCartState.StateType.productList.contains(gn);
    }

    private static class StateType {
        public static final List<String> productList = List.of("ACTIVE", "DEACTIVATE");
    }
}
