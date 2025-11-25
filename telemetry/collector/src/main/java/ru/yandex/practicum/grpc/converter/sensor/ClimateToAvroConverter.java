package ru.yandex.practicum.grpc.converter.sensor;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.SensorEvent;

@Component
public class ClimateToAvroConverter {

    public SensorEvent convertToAvro(ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto proto) {
        Timestamp protoTimestamp = proto.getTimestamp();
        //Instant instant = Instant.ofEpochSecond(protoTimestamp.getSeconds(), protoTimestamp.getNanos());

        ClimateSensorEvent climatePayload = convert(proto.getClimateSensor());

        return SensorEvent.newBuilder()
                .setId(proto.getId())
                .setHubId(proto.getHubId())
                .setTimestamp(protoTimestamp.getSeconds())
                .setPayload(climatePayload)
                .build();
    }

    private ClimateSensorEvent convert(ru.yandex.practicum.grpc.telemetry.messages.ClimateSensorProto protoPayload) {
        return ClimateSensorEvent.newBuilder()
                .setTemperatureC(protoPayload.getTemperatureC())
                .setCo2Level(protoPayload.getCo2Level())
                .setHumidity(protoPayload.getHumidity())
                .build();
    }
}
