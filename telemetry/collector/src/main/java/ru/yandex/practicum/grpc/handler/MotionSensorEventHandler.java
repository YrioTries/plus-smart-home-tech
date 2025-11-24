package ru.yandex.practicum.grpc.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MotionSensorEventHandler implements SensorEventHandler {

    @Override
    public ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto.PayloadCase getMessageType() {
        return ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto.PayloadCase.MOTION_SENSOR;
    }

    @Override
    public void handle(ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto event) {
        var motionSensor = event.getMotionSensor();
        log.info("Обработка датчика движения: движение={}, качество связи={}, напряжение={}",
                motionSensor.getMotion(), motionSensor.getLinkQuality(), motionSensor.getVoltage());

        if (motionSensor.getMotion() && motionSensor.getLinkQuality() < 50) {
            log.warn("Низкое качество связи при обнаружении движения!");
        }
    }
}
