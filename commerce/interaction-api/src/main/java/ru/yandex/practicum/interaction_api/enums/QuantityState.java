package ru.yandex.practicum.interaction_api.enums;

import java.util.List;

public enum QuantityState {
    MANY(QuantityState.ProductNumber.productNumberList.get(0)),
    ENOUGH(QuantityState.ProductNumber.productNumberList.get(1)),
    FEW(QuantityState.ProductNumber.productNumberList.get(2)),
    ENDED(QuantityState.ProductNumber.productNumberList.get(3));

    private final String number;

    QuantityState(String number) {
        this.number = number;
    }

    public String getProduct() {
        return number;
    }

    public static boolean isCorrectProduct(String gn) {
        return QuantityState.ProductNumber.productNumberList.contains(gn);
    }

    private static class ProductNumber {
        public static final List<String> productNumberList = List.of(
                "осталось больше 100 единиц",
                "осталось от 10 до 100 единиц",
                "осталось меньше 10 единиц товара",
                "товар закончился"
        );
    }
}
