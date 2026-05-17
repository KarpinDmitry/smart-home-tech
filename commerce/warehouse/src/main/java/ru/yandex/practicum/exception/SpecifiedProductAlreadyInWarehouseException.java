package ru.yandex.practicum.exception;

import java.util.UUID;

public class SpecifiedProductAlreadyInWarehouseException extends RuntimeException {

    public SpecifiedProductAlreadyInWarehouseException(UUID productId) {
        super("Product with id %s already registered in warehouse".formatted(productId));
    }
}
