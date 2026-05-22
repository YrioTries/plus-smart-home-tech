package ru.yandex.practicum.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "ru.yandex.practicum.interaction_api")
public class PaymentStarter {
    public static void main(String[] args) {
        SpringApplication.run(PaymentStarter.class, args);
    }
}