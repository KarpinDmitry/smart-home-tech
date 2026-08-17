package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {

    PaymentDto payment(OrderDto order);

    BigDecimal getTotalCost(OrderDto order);

    BigDecimal productCost(OrderDto order);

    void paymentSuccess(UUID paymentId);

    void paymentFailed(UUID paymentId);
}
