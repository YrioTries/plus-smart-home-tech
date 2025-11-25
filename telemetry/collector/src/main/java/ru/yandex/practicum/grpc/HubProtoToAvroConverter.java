package ru.yandex.practicum.grpc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEvent;

@Service
@RequiredArgsConstructor
public class HubProtoToAvroConverter implements HubConverter {

    private final ScenarioAddedToAvroConverter scenarioAddedToAvroConverter;
    private final ScenarioRemoveToAvroConverter scenarioRemoveToAvroConverter;
    private final DeviceAddedToAvroConverter deviceAddedToAvroConverter;
    private final DeviceRemoveToAvroConverter deviceRemoveToAvroConverter;

    @Override
    public HubEvent convertToScenarioAdded(ru.yandex.practicum.grpc.telemetry.messages.HubEventProto proto) {
        return scenarioAddedToAvroConverter.convertToAvro(proto);
    }

    @Override
    public HubEvent convertToScenarioRemove(ru.yandex.practicum.grpc.telemetry.messages.HubEventProto proto) {
        return scenarioRemoveToAvroConverter.convertToAvro(proto);
    }

    @Override
    public HubEvent convertToDeviceAdded(ru.yandex.practicum.grpc.telemetry.messages.HubEventProto proto) {
        return deviceAddedToAvroConverter.convertToAvro(proto);
    }

    @Override
    public HubEvent convertToDeviceRemove(ru.yandex.practicum.grpc.telemetry.messages.HubEventProto proto) {
        return deviceRemoveToAvroConverter.convertToAvro(proto);
    }
}
