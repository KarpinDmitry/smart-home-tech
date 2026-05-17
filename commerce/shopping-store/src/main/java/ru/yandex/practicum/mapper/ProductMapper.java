package ru.yandex.practicum.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import ru.yandex.practicum.dto.PageProductDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SortObject;
import ru.yandex.practicum.model.Product;

import java.util.List;

@UtilityClass
public class ProductMapper {

    public static Product toProduct(ProductDto productDto){
        Product product = new Product();
        product.setProductName(productDto.getProductName());
        product.setDescription(productDto.getDescription());
        product.setImageSrc(productDto.getImageSrc());
        product.setPrice(productDto.getPrice());
        product.setQuantityState(productDto.getQuantityState());
        product.setProductCategory(productDto.getProductCategory());
        product.setProductState(productDto.getProductState());

        return product;
    }

    public static void updateEntity(Product target, ProductDto source) {
        target.setProductName(source.getProductName());
        target.setDescription(source.getDescription());
        target.setImageSrc(source.getImageSrc());
        target.setPrice(source.getPrice());
        target.setQuantityState(source.getQuantityState());
        target.setProductState(source.getProductState());
        target.setProductCategory(source.getProductCategory());
    }

    public static ProductDto toDto(Product product) {
        return ProductDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .imageSrc(product.getImageSrc())
                .price(product.getPrice())
                .quantityState(product.getQuantityState())
                .productState(product.getProductState())
                .productCategory(product.getProductCategory())
                .build();
    }

    public static SortObject toSortObject(Sort.Order order) {
        return SortObject.builder()
                .direction(order.getDirection().name())
                .property(order.getProperty())
                .ascending(order.isAscending())
                .ignoreCase(order.isIgnoreCase())
                .nullHandling(order.getNullHandling().name())
                .build();
    }

    public static PageProductDto toPageDto(Page<Product> page) {
        List<ProductDto> content = page.getContent().stream()
                .map(ProductMapper::toDto)
                .toList();

        List<SortObject> sort = page.getSort().stream()
                .map(ProductMapper::toSortObject)
                .toList();

        return PageProductDto.builder()
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .size(page.getSize())
                .number(page.getNumber())
                .numberOfElements(page.getNumberOfElements())
                .empty(page.isEmpty())
                .sort(sort)
                .content(content)
                .build();
    }
}
