package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.model.AddressEmbeddable;
import ru.yandex.practicum.model.DeliveryEntity;

public class DeliveryMapper {

    public static DeliveryDto toDto(DeliveryEntity entity) {
        DeliveryDto deliveryDto = new DeliveryDto();
        deliveryDto.setDeliveryId(entity.getDeliveryId());
        deliveryDto.setOrderId(entity.getOrderId());
        deliveryDto.setDeliveryState(entity.getDeliveryState());
        deliveryDto.setFromAddress(toAddressDto(entity.getFromAddress()));
        deliveryDto.setToAddress(toAddressDto(entity.getToAddress()));
        return deliveryDto;
    }

    public static AddressEmbeddable toEmbeddable(AddressDto addressDto) {
        if (addressDto == null) return null;
        AddressEmbeddable embeddable = new AddressEmbeddable();
        embeddable.setCountry(addressDto.getCountry());
        embeddable.setCity(addressDto.getCity());
        embeddable.setStreet(addressDto.getStreet());
        embeddable.setHouse(addressDto.getHouse());
        embeddable.setFlat(addressDto.getFlat());
        return embeddable;
    }

    public static AddressDto toAddressDto(AddressEmbeddable embeddable) {
        if (embeddable == null) return null;
        AddressDto addressDto = new AddressDto();
        addressDto.setCountry(embeddable.getCountry());
        addressDto.setCity(embeddable.getCity());
        addressDto.setStreet(embeddable.getStreet());
        addressDto.setHouse(embeddable.getHouse());
        addressDto.setFlat(embeddable.getFlat());
        return addressDto;
    }
}
