package ru.yandex.practicum.grpc.converter.sensor;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.SensorEvent;

@Component
public class LightToAvroConverter {

    public SensorEvent convertToAvro(ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto proto) {
        Timestamp protoTimestamp = proto.getTimestamp();
        //Instant instant = Instant.ofEpochSecond(protoTimestamp.getSeconds(), protoTimestamp.getNanos());

        LightSensorEvent lightPayload = convert(proto.getLightSensor());

        return SensorEvent.newBuilder()
                .setId(proto.getId())
                .setHubId(proto.getHubId())
                .setTimestamp(protoTimestamp.getSeconds())
                .setPayload(lightPayload)
                .build();
    }

    private LightSensorEvent convert(ru.yandex.practicum.grpc.telemetry.messages.LightSensorProto protoPayload) {
        return LightSensorEvent.newBuilder()
                .setLinkQuality(protoPayload.getLinkQuality())
                .setLuminosity(protoPayload.getLuminosity())
                .build();
    }
}
