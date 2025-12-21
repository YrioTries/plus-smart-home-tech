package ru.yandex.practicum.interaction_api.model.mappers;

import org.mapstruct.*;
import ru.yandex.practicum.interaction_api.model.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction_api.model.entity.ShoppingCartEntity;
import ru.yandex.practicum.interaction_api.model.entity.CartProductEntity;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ShoppingCartMapper {

    @Mapping(target = "shoppingCartId", source = "id")
    @Mapping(target = "products", source = "cartProducts", qualifiedByName = "mapCartProductsToIntegerList")
    ShoppingCartDto toDto(ShoppingCartEntity entity);


    @Named("mapCartProductsToIntegerList")
    default List<Integer> mapCartProductsToIntegerList(List<CartProductEntity> cartProducts) {
        if (cartProducts == null || cartProducts.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> result = new ArrayList<>();

        for (CartProductEntity cartProduct : cartProducts) {
            if (cartProduct == null || cartProduct.getProduct() == null ||
                    cartProduct.getQuantity() == null || cartProduct.getQuantity() <= 0) {
                continue;
            }

            String productId = cartProduct.getProduct().getId();
            int quantity = cartProduct.getQuantity();

            // Преобразуем String ID в Integer
            Integer productIdAsInteger;
            try {
                productIdAsInteger = Integer.parseInt(productId);
            } catch (NumberFormatException e) {
                // Если ID не число, используем хэш
                productIdAsInteger = productId.hashCode();
            }

            // Добавляем ID товара quantity раз
            for (int i = 0; i < quantity; i++) {
                result.add(productIdAsInteger);
            }
        }

        return result;
    }
}