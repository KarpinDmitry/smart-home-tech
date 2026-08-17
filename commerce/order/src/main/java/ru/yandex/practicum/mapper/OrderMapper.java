package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.model.OrderEntity;

public class OrderMapper {

    public static OrderDto toDto(OrderEntity entity) {
        OrderDto dto = new OrderDto();
        dto.setOrderId(entity.getOrderId());
        dto.setShoppingCartId(entity.getShoppingCartId());
        dto.setProducts(entity.getProducts());
        dto.setPaymentId(entity.getPaymentId());
        dto.setDeliveryId(entity.getDeliveryId());
        dto.setState(entity.getState());
        dto.setDeliveryWeight(entity.getDeliveryWeight());
        dto.setDeliveryVolume(entity.getDeliveryVolume());
        dto.setFragile(entity.getFragile());
        dto.setTotalPrice(entity.getTotalPrice());
        dto.setDeliveryPrice(entity.getDeliveryPrice());
        dto.setProductPrice(entity.getProductPrice());
        return dto;
    }
}
