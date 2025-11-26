package ru.yandex.practicum.grpc.converter.sensor;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.SensorEvent;

@Component
public class MotionToAvroConverter {

    public SensorEvent convertToAvro(ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto proto) {
        Timestamp protoTimestamp = proto.getTimestamp();
        //Instant instant = Instant.ofEpochSecond(protoTimestamp.getSeconds(), protoTimestamp.getNanos());

        MotionSensorEvent motionSensorAvro = convert(proto.getMotionSensor());

        return SensorEvent.newBuilder()
                .setId(proto.getId())
                .setHubId(proto.getHubId())
                .setTimestamp(protoTimestamp.getSeconds() + protoTimestamp.getNanos())
                .setPayload(motionSensorAvro)
                .build();
    }

    private MotionSensorEvent convert(ru.yandex.practicum.grpc.telemetry.messages.MotionSensorProto protoPayload) {
        return MotionSensorEvent.newBuilder()
                .setMotion(protoPayload.getMotion())
                .setLinkQuality(protoPayload.getLinkQuality())
                .setVoltage(protoPayload.getVoltage())
                .build();
    }

}
