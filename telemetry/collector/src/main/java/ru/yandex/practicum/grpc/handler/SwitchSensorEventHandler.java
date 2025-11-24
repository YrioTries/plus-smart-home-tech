package ru.yandex.practicum.grpc.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SwitchSensorEventHandler implements SensorEventHandler {

    @Override
    public ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto.PayloadCase getMessageType() {
        return ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto.PayloadCase.SWITCH_SENSOR;
    }

    @Override
    public void handle(ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto event) {
        var switchSensor = event.getSwitchSensor();
        log.info("Обработка переключателя: состояние={}", switchSensor.getState());

        if (switchSensor.getState()) {
            log.info("Переключатель активирован");
        } else {
            log.info("Переключатель деактивирован");
        }
    }
}
