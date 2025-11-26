package ru.yandex.practicum.grpc.converter.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.messages.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEvent;

@Service
@RequiredArgsConstructor
public class HubProtoToAvroConverter implements HubConverter {

    private final ScenarioAddedToAvroConverter scenarioAddedToAvroConverter;
    private final ScenarioRemoveToAvroConverter scenarioRemoveToAvroConverter;
    private final DeviceAddedToAvroConverter deviceAddedToAvroConverter;
    private final DeviceRemoveToAvroConverter deviceRemoveToAvroConverter;

    @Override
    public HubEvent convertToScenarioAdded(HubEventProto proto) {
        return scenarioAddedToAvroConverter.convertToAvro(proto);
    }

    @Override
    public HubEvent convertToScenarioRemove(HubEventProto proto) {
        return scenarioRemoveToAvroConverter.convertToAvro(proto);
    }

    @Override
    public HubEvent convertToDeviceAdded(HubEventProto proto) {
        return deviceAddedToAvroConverter.convertToAvro(proto);
    }

    @Override
    public HubEvent convertToDeviceRemove(HubEventProto proto) {
        return deviceRemoveToAvroConverter.convertToAvro(proto);
    }
}
