package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.shoppingStore.ShoppingStoreOperations;
import ru.yandex.practicum.dto.PageProductDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.service.ShoppingStoreService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
public class ShoppingStoreController implements ShoppingStoreOperations {

    private final ShoppingStoreService shoppingStoreService;

    @Override
    public PageProductDto getProducts(ProductCategory category, int page, int size, List<String> sort) {
        return shoppingStoreService.getProducts(category, page, size, sort);
    }

    @Override
    public ProductDto createNewProduct(@NotNull @Valid ProductDto productDto) {
        return shoppingStoreService.createNewProduct(productDto);
    }

    @Override
    public ProductDto updateProduct(@NotNull @Valid ProductDto productDto) {
        return shoppingStoreService.updateProduct(productDto);
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        return shoppingStoreService.getProduct(productId);
    }

    @Override
    public Boolean removeProductFromStore(UUID productId) {
        return shoppingStoreService.removeProductFromStore(productId);
    }

    @Override
    public Boolean setProductQuantityState(@NotNull @Valid SetProductQuantityStateRequest request) {
        return shoppingStoreService.setProductQuantityState(request);
    }
}
