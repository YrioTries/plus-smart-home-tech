package ru.yandex.practicum.grpc;

import ru.yandex.practicum.kafka.telemetry.event.HubEvent;

public interface HubConverter {

    HubEvent convertToScenarioAdded(ru.yandex.practicum.grpc.telemetry.messages.HubEventProto proto);

    HubEvent convertToScenarioRemove(ru.yandex.practicum.grpc.telemetry.messages.HubEventProto proto);

    HubEvent convertToDeviceAdded(ru.yandex.practicum.grpc.telemetry.messages.HubEventProto proto);

    HubEvent convertToDeviceRemove(ru.yandex.practicum.grpc.telemetry.messages.HubEventProto proto);
}
