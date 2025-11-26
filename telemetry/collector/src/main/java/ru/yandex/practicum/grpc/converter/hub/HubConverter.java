package ru.yandex.practicum.grpc.converter.hub;

import ru.yandex.practicum.grpc.telemetry.messages.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubConverter {

    HubEventAvro convertToScenarioAdded(HubEventProto proto);

    HubEventAvro convertToScenarioRemove(HubEventProto proto);

    HubEventAvro convertToDeviceAdded(HubEventProto proto);

    HubEventAvro convertToDeviceRemove(HubEventProto proto);
}
