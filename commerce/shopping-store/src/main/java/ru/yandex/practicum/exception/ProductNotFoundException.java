package ru.yandex.practicum.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(UUID productId) {
        super("Product with id %s not found".formatted(productId));
    }
}
