package ru.yandex.practicum.grpc.converter.sensor;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorEvent;

@Component
public class TemperatureToAvroConverter {

    public SensorEvent convertToAvro(ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto proto) {
        Timestamp protoTimestamp = proto.getTimestamp();
        //Instant instant = Instant.ofEpochSecond(protoTimestamp.getSeconds(), protoTimestamp.getNanos());

        TemperatureSensorEvent temperaturePayload = convert(proto.getTemperatureSensor());

        return SensorEvent.newBuilder()
                .setId(proto.getId())
                .setHubId(proto.getHubId())
                .setTimestamp(protoTimestamp.getSeconds())
                .setPayload(temperaturePayload)
                .build();
    }

    private TemperatureSensorEvent convert(ru.yandex.practicum.grpc.telemetry.messages.TemperatureSensorProto protoPayload) {
        return TemperatureSensorEvent.newBuilder()
                .setTemperatureC(protoPayload.getTemperatureC())
                .setTemperatureF(protoPayload.getTemperatureF())
                .build();
    }
}
