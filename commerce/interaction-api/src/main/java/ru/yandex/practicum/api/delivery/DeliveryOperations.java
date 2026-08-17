package ru.yandex.practicum.api.delivery;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryOperations {

    @PutMapping
    DeliveryDto planDelivery(@RequestBody DeliveryDto delivery);

    @PostMapping("/cost")
    BigDecimal deliveryCost(@RequestBody OrderDto order);

    @PostMapping("/picked")
    void deliveryPicked(@RequestBody UUID orderId);

    @PostMapping("/successful")
    void deliverySuccessful(@RequestBody UUID orderId);

    @PostMapping("/failed")
    void deliveryFailed(@RequestBody UUID orderId);
}
