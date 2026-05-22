package ru.yandex.practicum.interaction_api.model.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.interaction_api.model.shopping_cart.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.AddressDto;

@Data
@Builder
public class CreateNewOrderRequest {

    @NotNull
    private ShoppingCartDto shoppingCart;

    @NotNull
    private AddressDto deliveryAddress;
}
