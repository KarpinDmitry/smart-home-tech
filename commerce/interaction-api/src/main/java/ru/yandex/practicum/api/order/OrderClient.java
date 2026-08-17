package ru.yandex.practicum.api.order;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient extends OrderOperations {
}
