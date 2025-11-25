package ru.yandex.practicum.grpc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEvent;

@Service
@RequiredArgsConstructor
public class SensorProtoToAvroConverter implements SensorConverter {

    private final ClimateToAvroConverter climateToAvroConverter;
    private final TemperatureToAvroConverter temperatureToAvroConverter;
    private final LightToAvroConverter lightToAvroConverter;
    private final SwitchToAvroConverter switchToAvroConverter;
    private final MotionToAvroConverter motionToAvroConverter;

    @Override
    public SensorEvent convertToClimateAvro(ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto proto) {
        return climateToAvroConverter.convertToAvro(proto);
    }

    @Override
    public SensorEvent convertToTemperatureAvro(ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto proto) {
        return temperatureToAvroConverter.convertToAvro(proto);
    }

    @Override
    public SensorEvent convertToLightAvro(ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto proto) {
        return lightToAvroConverter.convertToAvro(proto);
    }

    @Override
    public SensorEvent convertToSwitchAvro(ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto proto) {
        return switchToAvroConverter.convertToAvro(proto);
    }

    @Override
    public SensorEvent convertToMotionAvro(ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto proto) {
        return motionToAvroConverter.convertToAvro(proto);
    }
}
