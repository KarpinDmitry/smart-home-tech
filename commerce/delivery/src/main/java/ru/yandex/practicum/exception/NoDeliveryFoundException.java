package ru.yandex.practicum.exception;

import java.util.UUID;

public class NoDeliveryFoundException extends RuntimeException {

    public NoDeliveryFoundException(UUID id) {
        super("Delivery not found for: %s".formatted(id));
    }
}
