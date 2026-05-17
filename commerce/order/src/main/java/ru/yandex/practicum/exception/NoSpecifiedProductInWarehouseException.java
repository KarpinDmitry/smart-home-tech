package ru.yandex.practicum.exception;

import java.util.UUID;

public class NoSpecifiedProductInWarehouseException extends RuntimeException {

    public NoSpecifiedProductInWarehouseException(UUID productId) {
        super("No product in warehouse: %s".formatted(productId));
    }
}
