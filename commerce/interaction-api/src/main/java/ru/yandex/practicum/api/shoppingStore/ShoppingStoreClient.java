package ru.yandex.practicum.api.shoppingStore;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreClient extends ShoppingStoreOperations {
}
