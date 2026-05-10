package ru.yandex.practicum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.enums.QuantityState;

import java.util.UUID;

@Data
public class SetProductQuantityStateRequest {
    @NotNull
    private UUID productId;
    @NotNull
    private QuantityState quantityState;
}
