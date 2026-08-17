package ru.yandex.practicum.exception;

import java.util.UUID;

public class NoOrderFoundException extends RuntimeException {

    public NoOrderFoundException(UUID id) {
        super("Payment not found: %s".formatted(id));
    }
}
