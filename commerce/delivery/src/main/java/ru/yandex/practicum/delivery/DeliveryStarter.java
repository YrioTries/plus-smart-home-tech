package ru.yandex.practicum.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "ru.yandex.practicum.interaction_api")
public class DeliveryStarter {
    public static void main(String[] args) {
        SpringApplication.run(DeliveryStarter.class, args);
    }
}
