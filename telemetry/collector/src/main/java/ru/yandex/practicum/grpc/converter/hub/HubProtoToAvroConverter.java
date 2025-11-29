package ru.yandex.practicum.grpc.converter.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.converter.hub.processing.DeviceAddedToAvroConverter;
import ru.yandex.practicum.grpc.converter.hub.processing.DeviceRemoveToAvroConverter;
import ru.yandex.practicum.grpc.converter.hub.processing.ScenarioAddedToAvroConverter;
import ru.yandex.practicum.grpc.converter.hub.processing.ScenarioRemoveToAvroConverter;
import ru.yandex.practicum.grpc.telemetry.messages.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Service
@RequiredArgsConstructor
public class HubProtoToAvroConverter implements HubConverter {

    private final ScenarioAddedToAvroConverter scenarioAddedToAvroConverter;
    private final ScenarioRemoveToAvroConverter scenarioRemoveToAvroConverter;
    private final DeviceAddedToAvroConverter deviceAddedToAvroConverter;
    private final DeviceRemoveToAvroConverter deviceRemoveToAvroConverter;

    @Override
    public HubEventAvro convertToScenarioAdded(HubEventProto proto) {
        return scenarioAddedToAvroConverter.convertToAvro(proto);
    }

    @Override
    public HubEventAvro convertToScenarioRemove(HubEventProto proto) {
        return scenarioRemoveToAvroConverter.convertToAvro(proto);
    }

    @Override
    public HubEventAvro convertToDeviceAdded(HubEventProto proto) {
        return deviceAddedToAvroConverter.convertToAvro(proto);
    }

    @Override
    public HubEventAvro convertToDeviceRemove(HubEventProto proto) {
        return deviceRemoveToAvroConverter.convertToAvro(proto);
    }
}
