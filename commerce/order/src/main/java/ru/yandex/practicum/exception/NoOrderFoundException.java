package ru.yandex.practicum.exception;

import java.util.UUID;

public class NoOrderFoundException extends RuntimeException {

    public NoOrderFoundException(UUID orderId) {
        super("Order not found: %s".formatted(orderId));
    }
}
