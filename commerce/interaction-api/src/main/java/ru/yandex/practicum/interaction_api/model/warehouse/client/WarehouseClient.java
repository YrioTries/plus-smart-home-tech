package ru.yandex.practicum.interaction_api.model.warehouse.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.AddressDto;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.interaction_api.model.shopping_cart.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.request.ShippedToDeliveryRequest;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse")
public interface WarehouseClient {

    @PostMapping("/api/v1/warehouse/assembly")
    BookedProductsDto assemblyProductForOrderFromShoppingCart(@RequestBody @Valid ShoppingCartDto shoppingCartDto);

    @GetMapping("/api/v1/warehouse/address")
    AddressDto getWarehouseAddress();

    @PostMapping("/api/v1/warehouse/returnProducts")
    void returnProducts(@RequestBody Map<UUID, Integer> products);

    @PostMapping("/api/v1/warehouse/assembly")
    BookedProductsDto assemblyProducts(@RequestBody @Valid AssemblyProductsForOrderRequest request);

    @PostMapping("/api/v1/warehouse/shipped")
    void shippedOrder(@RequestBody @Valid ShippedToDeliveryRequest request);

    @PostMapping("/api/v1/warehouse/check")
    BookedProductsDto checkQuantityForCart(@RequestBody ShoppingCartDto shoppingCart);
}
