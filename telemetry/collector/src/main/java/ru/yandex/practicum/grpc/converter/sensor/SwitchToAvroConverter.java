package ru.yandex.practicum.grpc.converter.sensor;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorEvent;

@Component
public class SwitchToAvroConverter {

    public SensorEvent convertToAvro(ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto proto) {
        Timestamp protoTimestamp = proto.getTimestamp();
        //Instant instant = Instant.ofEpochSecond(protoTimestamp.getSeconds(), protoTimestamp.getNanos());

        SwitchSensorEvent climatePayload = convert(proto.getSwitchSensor());

        return SensorEvent.newBuilder()
                .setId(proto.getId())
                .setHubId(proto.getHubId())
                .setTimestamp(protoTimestamp.getSeconds())
                .setPayload(climatePayload)
                .build();
    }

    private SwitchSensorEvent convert(ru.yandex.practicum.grpc.telemetry.messages.SwitchSensorProto protoPayload) {
        return SwitchSensorEvent.newBuilder()
                .setState(protoPayload.getState())
                .build();
    }
}
