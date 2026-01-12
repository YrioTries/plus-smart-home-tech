package ru.yandex.practicum.delivery.model.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryAddress {
    private String country;
    private String city;
    private String street;
    private String house;
    private String flat;
}
