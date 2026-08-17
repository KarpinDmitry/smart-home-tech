package ru.yandex.practicum.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateNewOrderRequest {
    @NotBlank
    private String username;
    @NotNull
    @Valid
    private ShoppingCartDto shoppingCart;
    @NotNull
    @Valid
    private AddressDto deliveryAddress;
}
