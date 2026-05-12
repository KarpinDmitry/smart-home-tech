package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.shoppingStore.ShoppingStoreClient;
import ru.yandex.practicum.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.enums.QuantityState;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseProductMapper;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseProductRepository;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private static final AddressDto[] ADDRESSES = {
            buildAddress("ADDRESS_1"),
            buildAddress("ADDRESS_2")
    };
    private static final AddressDto WAREHOUSE_ADDRESS =
            ADDRESSES[new Random().nextInt(ADDRESSES.length)];

    private final WarehouseProductRepository productRepository;
    private final ShoppingStoreClient shoppingStoreClient;

    @Override
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        if (productRepository.existsById(request.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException(request.getProductId());
        }
        productRepository.save(WarehouseProductMapper.toEntity(request));
    }

    @Override
    @Transactional(readOnly = true)
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cart) {
        Map<UUID, Long> requested = cart.getProducts();

        Map<UUID, WarehouseProduct> stored = productRepository.findAllById(requested.keySet()).stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        double totalWeight = 0d;
        double totalVolume = 0d;
        boolean hasFragile = false;

        for (Map.Entry<UUID, Long> entry : requested.entrySet()) {
            UUID productId = entry.getKey();
            long needed = entry.getValue();

            WarehouseProduct product = stored.get(productId);
            if (product == null || product.getQuantity() < needed) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(productId);
            }

            totalWeight += product.getWeight() * needed;
            totalVolume += product.getWidth() * product.getHeight() * product.getDepth() * needed;
            hasFragile = hasFragile || product.isFragile();
        }

        BookedProductsDto booked = new BookedProductsDto();
        booked.setDeliveryWeight(totalWeight);
        booked.setDeliveryVolume(totalVolume);
        booked.setFragile(hasFragile);
        return booked;
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(request.getProductId()));

        product.setQuantity(product.getQuantity() + request.getQuantity());

        SetProductQuantityStateRequest stateRequest = new SetProductQuantityStateRequest();
        stateRequest.setProductId(product.getProductId());
        stateRequest.setQuantityState(computeQuantityState(product.getQuantity()));
        shoppingStoreClient.setProductQuantityState(stateRequest);
    }

    private static QuantityState computeQuantityState(long quantity) {
        if (quantity <= 0) return QuantityState.ENDED;
        if (quantity < 10) return QuantityState.FEW;
        if (quantity <= 100) return QuantityState.ENOUGH;
        return QuantityState.MANY;
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDto getWarehouseAddress() {
        return WAREHOUSE_ADDRESS;
    }

    private static AddressDto buildAddress(String label) {
        AddressDto address = new AddressDto();
        address.setCountry(label);
        address.setCity(label);
        address.setStreet(label);
        address.setHouse(label);
        address.setFlat(label);
        return address;
    }
}
