package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class AssemblyProductsForOrderRequest {
    @NotNull
    private Map<UUID, Long> products;
    @NotNull
    private UUID orderId;
}
