package ru.yandex.practicum.warehouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.AddressDto;

import java.security.SecureRandom;
import java.util.Random;

@SpringBootApplication
@EnableFeignClients(basePackages = "ru.yandex.practicum.interaction_api")
@EntityScan({
        "ru.yandex.practicum.warehouse.entity",
        "ru.yandex.practicum.shopping_store.entity"
})
public class WarehouseApplication {
    private static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};

    public static AddressDto getRandomAddress() {
        String current = ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

        return AddressDto.builder()
                .country(current)
                .city(current)
                .street(current)
                .house(current)
                .flat(current)
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(WarehouseApplication.class, args);
    }
}
