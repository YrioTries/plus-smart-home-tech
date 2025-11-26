package ru.yandex.practicum.grpc.converter.sensor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.converter.sensor.processing.*;
import ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Service
@RequiredArgsConstructor
public class SensorProtoToAvroConverter implements SensorConverter {

    private final ClimateToAvroConverter climateToAvroConverter;
    private final TemperatureToAvroConverter temperatureToAvroConverter;
    private final LightToAvroConverter lightToAvroConverter;
    private final SwitchToAvroConverter switchToAvroConverter;
    private final MotionToAvroConverter motionToAvroConverter;

    @Override
    public SensorEventAvro convertToClimateAvro(SensorEventProto proto) {
        return climateToAvroConverter.convertToAvro(proto);
    }

    @Override
    public SensorEventAvro convertToTemperatureAvro(SensorEventProto proto) {
        return temperatureToAvroConverter.convertToAvro(proto);
    }

    @Override
    public SensorEventAvro convertToLightAvro(SensorEventProto proto) {
        return lightToAvroConverter.convertToAvro(proto);
    }

    @Override
    public SensorEventAvro convertToSwitchAvro(SensorEventProto proto) {
        return switchToAvroConverter.convertToAvro(proto);
    }

    @Override
    public SensorEventAvro convertToMotionAvro(SensorEventProto proto) {
        return motionToAvroConverter.convertToAvro(proto);
    }
}
