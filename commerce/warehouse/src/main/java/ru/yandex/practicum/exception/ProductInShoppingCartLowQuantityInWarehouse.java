package ru.yandex.practicum.exception;

import java.util.UUID;

public class ProductInShoppingCartLowQuantityInWarehouse extends RuntimeException {

    public ProductInShoppingCartLowQuantityInWarehouse(UUID productId) {
        super("Product %s has insufficient quantity in warehouse".formatted(productId));
    }
}
