package ru.yandex.practicum.grpc.converter.sensor.processing;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.messages.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
public class LightToAvroConverter {

    public SensorEventAvro convertToAvro(SensorEventProto proto) {
        Timestamp protoTimestamp = proto.getTimestamp();
        //Instant instant = Instant.ofEpochSecond(protoTimestamp.getSeconds(), protoTimestamp.getNanos());

        LightSensorEventAvro lightPayload = convert(proto.getLightSensor());

        return SensorEventAvro.newBuilder()
                .setId(proto.getId())
                .setHubId(proto.getHubId())
                .setTimestamp(protoTimestamp.getSeconds() + protoTimestamp.getNanos())
                .setPayload(lightPayload)
                .build();
    }

    private LightSensorEventAvro convert(LightSensorProto protoPayload) {
        return LightSensorEventAvro.newBuilder()
                .setLinkQuality(protoPayload.getLinkQuality())
                .setLuminosity(protoPayload.getLuminosity())
                .build();
    }
}
