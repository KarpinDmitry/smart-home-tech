package ru.yandex.practicum.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DimensionDto {
    @NotNull
    @DecimalMin("1")
    private Double width;
    @NotNull
    @DecimalMin("1")
    private Double height;
    @NotNull
    @DecimalMin("1")
    private Double depth;
}
