package ru.yandex.practicum.warehouse.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.interaction_api.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.interaction_api.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.interaction_api.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.interaction_api.model.dto.AddressDto;
import ru.yandex.practicum.interaction_api.model.dto.BookedProductsDto;
import ru.yandex.practicum.interaction_api.model.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.dto.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction_api.model.dto.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.entity.WarehouseProductEntity;
import ru.yandex.practicum.warehouse.repositories.WarehouseRepository;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService{
    private final WarehouseRepository warehouseRepository;

    private static final String[] ADDRESSES =
            new String[] {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS =
            ADDRESSES[new SecureRandom().nextInt(ADDRESSES.length)];

    @Override
    public AddressDto getAddress() {
        AddressDto dto = new AddressDto();
        dto.setCountry(CURRENT_ADDRESS);
        dto.setCity(CURRENT_ADDRESS);
        dto.setStreet(CURRENT_ADDRESS);
        dto.setHouse(CURRENT_ADDRESS);
        dto.setFlat(CURRENT_ADDRESS);
        return dto;
    }

    @Override
    public BookedProductsDto checkProductsWarehouse(ShoppingCartDto shoppingCartDto) {
        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean fragile = false;

        for (var entry : shoppingCartDto.getProducts().entrySet()) {
            String productId = entry.getKey();
            int qty = entry.getValue();

            WarehouseProductEntity wp = warehouseRepository.findById(productId)
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Товар не найден на складе: "
                            + productId));

            if (wp.getQuantity() < qty)
                throw new ProductInShoppingCartLowQuantityInWarehouse("Недостаточно товара на складе: "
                        + productId);

            double volumeOne = wp.getWidth() * wp.getHeight() * wp.getDepth();
            totalVolume += volumeOne * qty;
            totalWeight += wp.getWeight() * qty;
            if (wp.isFragile()) {
                fragile = true;
            }
        }

        BookedProductsDto bookedProductsDto = new BookedProductsDto();
        bookedProductsDto.setDeliveryWeight(totalWeight);
        bookedProductsDto.setDeliveryVolume(totalVolume);
        bookedProductsDto.setFragile(fragile);
        return bookedProductsDto;
    }

    @Override
    public void acceptProductToWareHouse(AddProductToWarehouseRequest request) {
        WarehouseProductEntity entity = warehouseRepository.findById(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(
                        "Товар не найден на складе: " + request.getProductId()));

        int current = entity.getQuantity() == null ? 0 : entity.getQuantity();
        entity.setQuantity(current + request.getQuantity());

        warehouseRepository.save(entity);
    }

    @Override
    public void addProductToWareHouse(NewProductInWarehouseRequest request) {
        if (warehouseRepository.existsById(request.getProductId()))
            throw new SpecifiedProductAlreadyInWarehouseException("Товар уже есть на складе: "
                    + request.getProductId());

        WarehouseProductEntity entity = new WarehouseProductEntity();
        entity.setProductId(request.getProductId());
        entity.setFragile(request.isFragile());
        entity.setWidth(request.getDimension().getWidth());
        entity.setHeight(request.getDimension().getHeight());
        entity.setDepth(request.getDimension().getDepth());
        entity.setWeight(request.getWeight());
        entity.setQuantity(0);

        warehouseRepository.save(entity);
    }
}
