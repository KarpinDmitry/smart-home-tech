package ru.yandex.practicum.api.shoppingCart;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartClient extends ShoppingCartOperations {
}
