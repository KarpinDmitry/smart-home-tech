package ru.yandex.practicum.model;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Embeddable
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressEmbeddable {
    String country;
    String city;
    String street;
    String house;
    String flat;
}
