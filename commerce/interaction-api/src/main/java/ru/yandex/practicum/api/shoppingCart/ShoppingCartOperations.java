package ru.yandex.practicum.api.shoppingCart;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/api/v1/shopping-cart")
public interface ShoppingCartOperations {

    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam String username);

    @PutMapping
    ShoppingCartDto addProductToShoppingCart(
            @RequestParam String username,
            @RequestBody Map<UUID, Long> products);

    @DeleteMapping
    void deactivateCurrentShoppingCart(@RequestParam String username);

    @PostMapping("/remove")
    ShoppingCartDto removeFromShoppingCart(
            @RequestParam String username,
            @RequestBody List<UUID> productIds);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductQuantity(
            @RequestParam String username,
            @RequestBody ChangeProductQuantityRequest request);
}
