package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {

    DeliveryDto planDelivery(DeliveryDto delivery);

    BigDecimal deliveryCost(OrderDto order);

    void deliveryPicked(UUID orderId);

    void deliverySuccessful(UUID orderId);

    void deliveryFailed(UUID orderId);
}
