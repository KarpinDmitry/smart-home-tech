package ru.yandex.practicum.exception;

import java.util.UUID;

public class NoSpecifiedProductInWarehouseException extends RuntimeException {

    public NoSpecifiedProductInWarehouseException(UUID productId) {
        super("No information about product %s in warehouse".formatted(productId));
    }
}
