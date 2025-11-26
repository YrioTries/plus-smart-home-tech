package ru.yandex.practicum.grpc.converter.hub;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEvent;
import ru.yandex.practicum.kafka.telemetry.event.DeviceType;
import ru.yandex.practicum.kafka.telemetry.event.HubEvent;

@Component
public class DeviceAddedToAvroConverter {

    public HubEvent convertToAvro(ru.yandex.practicum.grpc.telemetry.messages.HubEventProto proto) {
        Timestamp protoTimestamp = proto.getTimestamp();
        //Instant instant = Instant.ofEpochSecond(protoTimestamp.getSeconds(), protoTimestamp.getNanos());

        DeviceAddedEvent deviceAddedEventAvro = finalConvert(proto.getDeviceAdded());

        return HubEvent.newBuilder()
                .setHubId(proto.getHubId())
                .setTimestamp(protoTimestamp.getSeconds() + protoTimestamp.getNanos())
                .setPayload(deviceAddedEventAvro)
                .build();
    }

    private DeviceAddedEvent finalConvert(ru.yandex.practicum.grpc.telemetry.messages.DeviceAddedEventProto proto) {
        return DeviceAddedEvent.newBuilder()
                .setId(proto.getId())
                .setDeviceType(convertDeviceType(proto.getType()))
                .build();
    }

    private DeviceType convertDeviceType(ru.yandex.practicum.grpc.telemetry.messages.DeviceTypeProto type) {
        return switch (type) {
            case MOTION_SENSOR -> DeviceType.MOTION_SENSOR;
            case TEMPERATURE_SENSOR -> DeviceType.TEMPERATURE_SENSOR;
            case LIGHT_SENSOR -> DeviceType.LIGHT_SENSOR;
            case CLIMATE_SENSOR -> DeviceType.CLIMATE_SENSOR;
            case SWITCH_SENSOR -> DeviceType.SWITCH_SENSOR;
            default -> throw new IndexOutOfBoundsException("Device type " + type + " not found");
        };
    }
}
