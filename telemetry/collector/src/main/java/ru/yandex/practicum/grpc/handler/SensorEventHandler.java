package ru.yandex.practicum.grpc.handler;

public interface SensorEventHandler {
    ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto.PayloadCase getMessageType();
    void handle(ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto event);
}
