package ru.yandex.practicum.grpc.converter.sensor;

import ru.yandex.practicum.kafka.telemetry.event.SensorEvent;

public interface SensorConverter {
    SensorEvent convertToClimateAvro(ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto proto);
    SensorEvent convertToTemperatureAvro(ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto proto);
    SensorEvent convertToLightAvro(ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto proto);
    SensorEvent convertToSwitchAvro(ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto proto);
    SensorEvent convertToMotionAvro(ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto proto);
}
