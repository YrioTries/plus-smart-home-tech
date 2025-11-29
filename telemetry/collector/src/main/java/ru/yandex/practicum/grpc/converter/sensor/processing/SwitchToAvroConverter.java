package ru.yandex.practicum.grpc.converter.sensor.processing;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.messages.SwitchSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorEventAvro;

@Component
public class SwitchToAvroConverter {

    public SensorEventAvro convertToAvro(SensorEventProto proto) {
        Timestamp protoTimestamp = proto.getTimestamp();
        //Instant instant = Instant.ofEpochSecond(protoTimestamp.getSeconds(), protoTimestamp.getNanos());

        SwitchSensorEventAvro climatePayload = convert(proto.getSwitchSensor());

        return SensorEventAvro.newBuilder()
                .setId(proto.getId())
                .setHubId(proto.getHubId())
                .setTimestamp(protoTimestamp.getSeconds() + protoTimestamp.getNanos())
                .setPayload(climatePayload)
                .build();
    }

    private SwitchSensorEventAvro convert(SwitchSensorProto protoPayload) {
        return SwitchSensorEventAvro.newBuilder()
                .setState(protoPayload.getState())
                .build();
    }
}
