package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import java.util.stream.Collectors;

@Component
public class SensorEventMapper {

    public SensorEvent toAvro(SensorEventDto eventDto) {
        SensorEvent.Builder sensorEventBuilder = SensorEvent.newBuilder()
                .setId(eventDto.getId())
                .setHubId(eventDto.getHubId())
                .setTimestamp(eventDto.getTimestamp().toEpochMilli());

        Object payload = createPayload(eventDto);
        sensorEventBuilder.setPayload(payload);

        return sensorEventBuilder.build();
    }

    private Object createPayload(SensorEventDto eventDto) {
        switch (eventDto.getType()) {
            case SWITCH_SENSOR_EVENT: {
                SwitchSensorEventDto switchDto = (SwitchSensorEventDto) eventDto;
                return SwitchSensorEvent.newBuilder()
                        .setState(switchDto.getState())
                        .build();
            }
            case CLIMATE_SENSOR_EVENT: {
                ClimateSensorEventDto climateDto = (ClimateSensorEventDto) eventDto;
                return ClimateSensorEvent.newBuilder()
                        .setTemperatureC(climateDto.getTemperatureC())
                        .setHumidity(climateDto.getHumidity())
                        .setCo2Level(climateDto.getCo2Level())
                        .build();
            }
            case LIGHT_SENSOR_EVENT: {
                LightSensorEventDto lightDto = (LightSensorEventDto) eventDto;
                return LightSensorEvent.newBuilder()
                        .setLinkQuality(lightDto.getLinkQuality())
                        .setLuminosity(lightDto.getLuminosity())
                        .build();
            }
            case MOTION_SENSOR_EVENT: {
                MotionSensorEventDto motionDto = (MotionSensorEventDto) eventDto;
                return MotionSensorEvent.newBuilder()
                        .setLinkQuality(motionDto.getLinkQuality())
                        .setMotion(motionDto.getMotion())
                        .setVoltage(motionDto.getVoltage())
                        .build();
            }
            case TEMPERATURE_SENSOR_EVENT: {
                TemperatureSensorEventDto tempDto = (TemperatureSensorEventDto) eventDto;
                return TemperatureSensorEvent.newBuilder()
                        .setTemperatureC(tempDto.getTemperatureC())
                        .setTemperatureF(tempDto.getTemperatureF())
                        .build();
            }
            default:
                throw new IllegalArgumentException("Unknown sensor event type: " + eventDto.getType());
        }
    }
}