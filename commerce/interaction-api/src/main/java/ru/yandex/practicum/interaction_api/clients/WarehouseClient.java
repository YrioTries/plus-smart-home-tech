package ru.yandex.practicum.interaction_api.clients;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.AddressDto;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction_api.model.dto.shopping_cart.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.NewProductInWarehouseRequest;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse")
public interface WarehouseClient {

    @GetMapping("/api/v1/warehouse/check")
    BookedProductsDto checkQuantityForCart(@RequestBody ShoppingCartDto shoppingCartDto);
}
