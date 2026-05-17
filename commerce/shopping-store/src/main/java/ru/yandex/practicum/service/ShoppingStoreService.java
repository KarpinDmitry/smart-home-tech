package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.PageProductDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.enums.ProductCategory;

import java.util.List;
import java.util.UUID;

public interface ShoppingStoreService {

    PageProductDto getProducts(ProductCategory category, int page, int size, List<String> sort);

    ProductDto createNewProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    ProductDto getProduct(UUID productId);

    Boolean removeProductFromStore(UUID productId);

    Boolean setProductQuantityState(SetProductQuantityStateRequest request);
}
