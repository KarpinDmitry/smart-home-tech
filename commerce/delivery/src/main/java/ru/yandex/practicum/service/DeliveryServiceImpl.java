package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.order.OrderClient;
import ru.yandex.practicum.api.warehouse.WarehouseClient;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.enums.DeliveryState;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.DeliveryEntity;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    @Override
    public DeliveryDto planDelivery(DeliveryDto dto) {
        DeliveryEntity entity = new DeliveryEntity();
        entity.setFromAddress(DeliveryMapper.toEmbeddable(dto.getFromAddress()));
        entity.setToAddress(DeliveryMapper.toEmbeddable(dto.getToAddress()));
        entity.setOrderId(dto.getOrderId());
        entity.setDeliveryState(DeliveryState.CREATED);
        deliveryRepository.save(entity);
        return DeliveryMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal deliveryCost(OrderDto order) {
        DeliveryEntity delivery = deliveryRepository.findByOrderId(order.getOrderId())
                .orElseThrow(() -> new NoDeliveryFoundException(order.getOrderId()));

        String warehouseLabel = delivery.getFromAddress().getStreet();
        double base = 5.0;

        double result;
        if (warehouseLabel != null && warehouseLabel.contains("ADDRESS_2")) {
            result = base * 2;
        } else {
            result = base;
        }
        result += base;

        if (Boolean.TRUE.equals(order.getFragile())) {
            result += result * 0.2;
        }

        double weight = order.getDeliveryWeight() != null ? order.getDeliveryWeight() : 0.0;
        result += weight * 0.3;

        double volume = order.getDeliveryVolume() != null ? order.getDeliveryVolume() : 0.0;
        result += volume * 0.2;

        String warehouseStreet = delivery.getFromAddress().getStreet();
        String deliveryStreet = delivery.getToAddress().getStreet();
        if (warehouseStreet == null || !warehouseStreet.equals(deliveryStreet)) {
            result += result * 0.2;
        }

        return BigDecimal.valueOf(result).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public void deliveryPicked(UUID orderId) {
        DeliveryEntity delivery = findByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);

        orderClient.assembly(orderId);

        ShippedToDeliveryRequest request = new ShippedToDeliveryRequest();
        request.setOrderId(orderId);
        request.setDeliveryId(delivery.getDeliveryId());
        warehouseClient.shippedToDelivery(request);
    }

    @Override
    public void deliverySuccessful(UUID orderId) {
        DeliveryEntity delivery = findByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        orderClient.delivery(orderId);
    }

    @Override
    public void deliveryFailed(UUID orderId) {
        DeliveryEntity delivery = findByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        orderClient.deliveryFailed(orderId);
    }

    private DeliveryEntity findByOrderId(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException(orderId));
    }
}
