package ru.yandex.practicum.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.PageProductDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.ProductState;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingStoreServiceIml implements ShoppingStoreService{

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public PageProductDto getProducts(ProductCategory category, int page, int size, List<String> sort) {
        Pageable pageable = PageRequest.of(page, size, buildSort(sort));

        Page<Product> productPage = productRepository
                .findAllByProductCategoryAndProductState(category, ProductState.ACTIVE, pageable);

        return productMapper.toPageDto(productPage);
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        Product product = productRepository.save(productMapper.toProduct(productDto));
        return productMapper.toDto(product);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        Product product = getProductOrThrow(productDto.getProductId());

        productMapper.updateEntity(product, productDto);

        return productMapper.toDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProduct(UUID productId) {
        return productMapper.toDto(getProductOrThrow(productId));
    }

    @Override
    public Boolean removeProductFromStore(UUID productId) {
        Product product = getProductOrThrow(productId);
        product.setProductState(ProductState.DEACTIVATE);
        return true;
    }

    @Override
    public Boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        Product product = getProductOrThrow(request.getProductId());
        product.setQuantityState(request.getQuantityState());
        return true;
    }

    private Product getProductOrThrow(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    private static Sort buildSort(List<String> sort){
        if (sort == null || sort.isEmpty()) {
            return Sort.unsorted();
        }

        List<Sort.Order> orders = new ArrayList<>();

        for (String s: sort){
            String[] parts = s.split(",");

            String property = parts[0];

            Sort.Direction direction = Sort.Direction.ASC;

            if (parts.length > 1){
                direction = Sort.Direction.fromString(parts[1].trim());
            }

            orders.add(new Sort.Order(direction, property));
        }

        return Sort.by(orders);
    }
}
