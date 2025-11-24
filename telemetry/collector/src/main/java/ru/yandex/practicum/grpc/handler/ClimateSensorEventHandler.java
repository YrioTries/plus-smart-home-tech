package ru.yandex.practicum.grpc.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClimateSensorEventHandler implements SensorEventHandler {

    @Override
    public ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto.PayloadCase getMessageType() {
        return ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }

    @Override
    public void handle(ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto event) {
        var climateSensor = event.getClimateSensor();
        log.info("Обработка климатического датчика: температура={}°C, влажность={}%, CO2={}ppm",
                climateSensor.getTemperatureC(), climateSensor.getHumidity(), climateSensor.getCo2Level());

        if (climateSensor.getCo2Level() > 1000) {
            log.warn("Высокий уровень CO2! Необходимо проветривание.");
        }

        if (climateSensor.getHumidity() > 70) {
            log.warn("Высокая влажность! Возможна конденсация.");
        }
    }
}

