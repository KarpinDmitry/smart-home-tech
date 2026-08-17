package ru.yandex.practicum.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.enums.DeliveryState;

import java.util.UUID;

@Data
public class DeliveryDto {
    private UUID deliveryId;
    @NotNull
    @Valid
    private AddressDto fromAddress;
    @NotNull
    @Valid
    private AddressDto toAddress;
    @NotNull
    private UUID orderId;
    @NotNull
    private DeliveryState deliveryState;
}
