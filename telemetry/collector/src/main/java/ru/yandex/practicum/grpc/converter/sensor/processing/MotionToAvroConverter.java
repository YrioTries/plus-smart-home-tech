package ru.yandex.practicum.grpc.converter.sensor.processing;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.messages.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
public class MotionToAvroConverter {

    public SensorEventAvro convertToAvro(SensorEventProto proto) {
        Timestamp protoTimestamp = proto.getTimestamp();
        //Instant instant = Instant.ofEpochSecond(protoTimestamp.getSeconds(), protoTimestamp.getNanos());

        MotionSensorEventAvro motionSensorAvro = convert(proto.getMotionSensor());

        return SensorEventAvro.newBuilder()
                .setId(proto.getId())
                .setHubId(proto.getHubId())
                .setTimestamp(protoTimestamp.getSeconds() + protoTimestamp.getNanos())
                .setPayload(motionSensorAvro)
                .build();
    }

    private MotionSensorEventAvro convert(MotionSensorProto protoPayload) {
        return MotionSensorEventAvro.newBuilder()
                .setMotion(protoPayload.getMotion())
                .setLinkQuality(protoPayload.getLinkQuality())
                .setVoltage(protoPayload.getVoltage())
                .build();
    }

}
