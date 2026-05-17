package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.warehouse.WarehouseClient;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final WarehouseClient warehouseClient;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        validateUsername(username);
        ShoppingCart cart = getOrCreateCart(username);
        return ShoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Long> products) {
        validateUsername(username);
        ShoppingCart cart = getOrCreateCart(username);

        Map<UUID, Long> proposed = new HashMap<>(cart.getProducts());
        products.forEach((productId, quantity) -> proposed.merge(productId, quantity, Long::sum));
        checkAvailability(cart.getShoppingCartId(), proposed);

        products.forEach((productId, quantity) ->
                cart.getProducts().merge(productId, quantity, Long::sum));
        return ShoppingCartMapper.toDto(cart);
    }

    @Override
    public void deactivateCurrentShoppingCart(String username) {
        validateUsername(username);
        shoppingCartRepository.findByUsernameAndActiveTrue(username)
                .ifPresent(cart -> cart.setActive(false));
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> productIds) {
        validateUsername(username);
        ShoppingCart cart = getActiveCartOrThrow(username);

        List<UUID> missing = productIds.stream()
                .filter(id -> !cart.getProducts().containsKey(id))
                .toList();
        if (!missing.isEmpty()) {
            throw new NoProductsInShoppingCartException(
                    "Products not in cart: " + missing);
        }

        productIds.forEach(cart.getProducts()::remove);
        return ShoppingCartMapper.toDto(cart);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        validateUsername(username);
        ShoppingCart cart = getActiveCartOrThrow(username);

        UUID productId = request.getProductId();
        if (!cart.getProducts().containsKey(productId)) {
            throw new NoProductsInShoppingCartException(
                    "Product " + productId + " not in cart");
        }

        Map<UUID, Long> proposed = new HashMap<>(cart.getProducts());
        proposed.put(productId, request.getNewQuantity());
        checkAvailability(cart.getShoppingCartId(), proposed);

        cart.getProducts().put(productId, request.getNewQuantity());
        return ShoppingCartMapper.toDto(cart);
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }
    }

    private ShoppingCart getOrCreateCart(String username) {
        return shoppingCartRepository.findByUsernameAndActiveTrue(username)
                .orElseGet(() -> {
                    ShoppingCart cart = new ShoppingCart();
                    cart.setUsername(username);
                    cart.setActive(true);
                    return shoppingCartRepository.save(cart);
                });
    }

    private ShoppingCart getActiveCartOrThrow(String username) {
        return shoppingCartRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new NoProductsInShoppingCartException(
                        "Active shopping cart not found for user " + username));
    }

    private void checkAvailability(UUID cartId, Map<UUID, Long> products) {
        if (products.isEmpty()) {
            return;
        }
        ShoppingCartDto check = new ShoppingCartDto();
        check.setShoppingCartId(cartId);
        check.setProducts(products);
        warehouseClient.checkProductQuantityEnoughForShoppingCart(check);
    }
}
