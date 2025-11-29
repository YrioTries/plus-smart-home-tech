package ru.yandex.practicum.grpc.converter.sensor.processing;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.messages.TemperatureSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorEventAvro;

@Component
public class TemperatureToAvroConverter {

    public SensorEventAvro convertToAvro(SensorEventProto proto) {
        Timestamp protoTimestamp = proto.getTimestamp();
        //Instant instant = Instant.ofEpochSecond(protoTimestamp.getSeconds(), protoTimestamp.getNanos());

        TemperatureSensorEventAvro temperaturePayload = convert(proto.getTemperatureSensor());

        return SensorEventAvro.newBuilder()
                .setId(proto.getId())
                .setHubId(proto.getHubId())
                .setTimestamp(protoTimestamp.getSeconds() + protoTimestamp.getNanos())
                .setPayload(temperaturePayload)
                .build();
    }

    private TemperatureSensorEventAvro convert(TemperatureSensorProto protoPayload) {
        return TemperatureSensorEventAvro.newBuilder()
                .setTemperatureC(protoPayload.getTemperatureC())
                .setTemperatureF(protoPayload.getTemperatureF())
                .build();
    }
}
