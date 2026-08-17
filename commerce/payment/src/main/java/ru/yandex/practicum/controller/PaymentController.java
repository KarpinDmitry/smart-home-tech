package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.payment.PaymentOperations;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController implements PaymentOperations {

    private final PaymentService paymentService;

    @Override
    public PaymentDto payment(OrderDto order) {
        return paymentService.payment(order);
    }

    @Override
    public BigDecimal getTotalCost(OrderDto order) {
        return paymentService.getTotalCost(order);
    }

    @Override
    public BigDecimal productCost(OrderDto order) {
        return paymentService.productCost(order);
    }

    @Override
    public void paymentSuccess(UUID paymentId) {
        paymentService.paymentSuccess(paymentId);
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        paymentService.paymentFailed(paymentId);
    }
}
