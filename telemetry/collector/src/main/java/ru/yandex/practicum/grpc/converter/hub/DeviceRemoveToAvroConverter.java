package ru.yandex.practicum.grpc.converter.hub;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEvent;

@Component
public class DeviceRemoveToAvroConverter {

    public HubEvent convertToAvro(ru.yandex.practicum.grpc.telemetry.messages.HubEventProto proto) {
        Timestamp protoTimestamp = proto.getTimestamp();
        //Instant instant = Instant.ofEpochSecond(protoTimestamp.getSeconds(), protoTimestamp.getNanos());

        DeviceRemovedEvent deviceRemoveToAvroConverter = convert(proto.getDeviceRemoved());

        return HubEvent.newBuilder()
                .setHubId(proto.getHubId())
                .setTimestamp(protoTimestamp.getSeconds() + protoTimestamp.getNanos())
                .setPayload(deviceRemoveToAvroConverter)
                .build();
    }

    private DeviceRemovedEvent convert(ru.yandex.practicum.grpc.telemetry.messages.DeviceRemovedEventProto proto) {
        return DeviceRemovedEvent.newBuilder()
                .setId(proto.getId())
                .build();
    }
}
