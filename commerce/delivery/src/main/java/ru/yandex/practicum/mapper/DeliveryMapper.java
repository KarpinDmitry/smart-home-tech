package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.model.AddressEmbeddable;
import ru.yandex.practicum.model.DeliveryEntity;

public class DeliveryMapper {

    public static DeliveryDto toDto(DeliveryEntity entity) {
        DeliveryDto dto = new DeliveryDto();
        dto.setDeliveryId(entity.getDeliveryId());
        dto.setOrderId(entity.getOrderId());
        dto.setDeliveryState(entity.getDeliveryState());
        dto.setFromAddress(toAddressDto(entity.getFromAddress()));
        dto.setToAddress(toAddressDto(entity.getToAddress()));
        return dto;
    }

    public static AddressEmbeddable toEmbeddable(AddressDto dto) {
        if (dto == null) return null;
        AddressEmbeddable embeddable = new AddressEmbeddable();
        embeddable.setCountry(dto.getCountry());
        embeddable.setCity(dto.getCity());
        embeddable.setStreet(dto.getStreet());
        embeddable.setHouse(dto.getHouse());
        embeddable.setFlat(dto.getFlat());
        return embeddable;
    }

    public static AddressDto toAddressDto(AddressEmbeddable embeddable) {
        if (embeddable == null) return null;
        AddressDto dto = new AddressDto();
        dto.setCountry(embeddable.getCountry());
        dto.setCity(embeddable.getCity());
        dto.setStreet(embeddable.getStreet());
        dto.setHouse(embeddable.getHouse());
        dto.setFlat(embeddable.getFlat());
        return dto;
    }
}
