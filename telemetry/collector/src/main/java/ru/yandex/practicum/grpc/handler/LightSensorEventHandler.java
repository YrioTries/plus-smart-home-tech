package ru.yandex.practicum.grpc.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LightSensorEventHandler implements SensorEventHandler {

    @Override
    public ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto.PayloadCase getMessageType() {
        return ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    public void handle(ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto event) {
        var lightSensor = event.getLightSensor();
        log.info("Обработка датчика освещенности: освещенность={}, качество связи={}",
                lightSensor.getLuminosity(), lightSensor.getLinkQuality());

        if (lightSensor.getLuminosity() < 100) {
            log.info("Низкая освещенность - возможно ночное время");
        } else if (lightSensor.getLuminosity() > 800) {
            log.info("Высокая освещенность - возможно прямой солнечный свет");
        }
    }
}
