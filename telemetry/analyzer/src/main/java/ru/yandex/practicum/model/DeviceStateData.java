package ru.yandex.practicum.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;

@Getter
@Builder
@ToString
public class DeviceStateData {
    private String deviceId;
    private long timestamp;
    private SensorStateAvro sensorState;
}
