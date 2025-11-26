package ru.yandex.practicum.grpc.converter.sensor.processing;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.messages.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
public class ClimateToAvroConverter {

    public SensorEventAvro convertToAvro(SensorEventProto proto) {
        Timestamp protoTimestamp = proto.getTimestamp();
        //Instant instant = Instant.ofEpochSecond(protoTimestamp.getSeconds(), protoTimestamp.getNanos());

        ClimateSensorEventAvro climatePayload = convert(proto.getClimateSensor());

        return SensorEventAvro.newBuilder()
                .setId(proto.getId())
                .setHubId(proto.getHubId())
                .setTimestamp(protoTimestamp.getSeconds() + protoTimestamp.getNanos())
                .setPayload(climatePayload)
                .build();
    }

    private ClimateSensorEventAvro convert(ClimateSensorProto protoPayload) {
        return ClimateSensorEventAvro.newBuilder()
                .setTemperatureC(protoPayload.getTemperatureC())
                .setCo2Level(protoPayload.getCo2Level())
                .setHumidity(protoPayload.getHumidity())
                .build();
    }
}
