package ru.yandex.practicum.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class NewProductInWarehouseRequest {
    @NotNull
    private UUID productId;
    private Boolean fragile;
    @NotNull
    @Valid
    private DimensionDto dimension;
    @NotNull
    @DecimalMin("1")
    private Double weight;
}
