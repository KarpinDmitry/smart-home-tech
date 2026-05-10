package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.warehouse.WarehouseOperations;
import ru.yandex.practicum.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.service.WarehouseService;

@RestController
@RequiredArgsConstructor
public class WarehouseController implements WarehouseOperations {

    private final WarehouseService warehouseService;

    @Override
    public void newProductInWarehouse(@Valid NewProductInWarehouseRequest request) {
        warehouseService.newProductInWarehouse(request);
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cart) {
        return warehouseService.checkProductQuantityEnoughForShoppingCart(cart);
    }

    @Override
    public void addProductToWarehouse(@Valid AddProductToWarehouseRequest request) {
        warehouseService.addProductToWarehouse(request);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return warehouseService.getWarehouseAddress();
    }
}
