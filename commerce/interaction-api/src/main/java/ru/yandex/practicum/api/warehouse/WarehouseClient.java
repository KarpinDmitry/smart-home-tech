package ru.yandex.practicum.api.warehouse;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "warehouse")
public interface WarehouseClient extends WarehouseOperations {
}
