package ru.yandex.practicum.api.payment;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentOperations {

    @PostMapping
    PaymentDto payment(@RequestBody OrderDto order);

    @PostMapping("/totalCost")
    BigDecimal getTotalCost(@RequestBody OrderDto order);

    @PostMapping("/productCost")
    BigDecimal productCost(@RequestBody OrderDto order);

    @PostMapping("/refund")
    void paymentSuccess(@RequestBody UUID paymentId);

    @PostMapping("/failed")
    void paymentFailed(@RequestBody UUID paymentId);
}
