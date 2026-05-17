package ru.yandex.practicum.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.model.ShoppingCart;

import java.util.HashMap;

@UtilityClass
public class ShoppingCartMapper {

    public static ShoppingCartDto toDto(ShoppingCart cart) {
        ShoppingCartDto dto = new ShoppingCartDto();
        dto.setShoppingCartId(cart.getShoppingCartId());
        dto.setProducts(new HashMap<>(cart.getProducts()));
        return dto;
    }
}
