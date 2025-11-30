package ru.yandex.practicum.grpc.converter.hub.processing;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.messages.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.messages.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.messages.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
public class DeviceAddedToAvroConverter {

    public HubEventAvro convertToAvro(HubEventProto proto) {
        Timestamp protoTimestamp = proto.getTimestamp();
        //Instant instant = Instant.ofEpochSecond(protoTimestamp.getSeconds(), protoTimestamp.getNanos());

        DeviceAddedEventAvro deviceAddedEventAvro = finalConvert(proto.getDeviceAdded());

        return HubEventAvro.newBuilder()
                .setHubId(proto.getHubId())
                .setTimestamp(protoTimestamp.getSeconds() + protoTimestamp.getNanos())
                .setPayload(deviceAddedEventAvro)
                .build();
    }

    private DeviceAddedEventAvro finalConvert(DeviceAddedEventProto proto) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(proto.getId())
                .setDeviceType(convertDeviceType(proto.getType()))
                .build();
    }

    private DeviceTypeAvro convertDeviceType(DeviceTypeProto type) {
        return switch (type) {
            case MOTION_SENSOR -> DeviceTypeAvro.MOTION_SENSOR;
            case TEMPERATURE_SENSOR -> DeviceTypeAvro.TEMPERATURE_SENSOR;
            case LIGHT_SENSOR -> DeviceTypeAvro.LIGHT_SENSOR;
            case CLIMATE_SENSOR -> DeviceTypeAvro.CLIMATE_SENSOR;
            case SWITCH_SENSOR -> DeviceTypeAvro.SWITCH_SENSOR;
            default -> throw new IndexOutOfBoundsException("Device type " + type + " not found");
        };
    }
}
