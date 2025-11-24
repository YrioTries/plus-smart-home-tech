package ru.yandex.practicum.grpc.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TemperatureSensorEventHandler implements SensorEventHandler {

    @Override
    public ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto.PayloadCase getMessageType() {
        return ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;
    }

    @Override
    public void handle(ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto event) {
        var tempSensor = event.getTemperatureSensor();
        log.info("Обработка датчика температуры: {}°C ({}°F)",
                tempSensor.getTemperatureC(), tempSensor.getTemperatureF());

        if (tempSensor.getTemperatureC() > 30) {
            log.warn("Высокая температура! Возможно перегрев.");
        } else if (tempSensor.getTemperatureC() < 10) {
            log.warn("Низкая температура! Возможно замерзание.");
        }
    }
}
