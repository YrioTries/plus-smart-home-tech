package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Component
public class SensorEventMapper {

    public SensorEventAvro toAvro(SensorEventDto eventDto) {
        SensorEventAvro.Builder sensorEventBuilder = SensorEventAvro.newBuilder()
                .setId(eventDto.getId())
                .setHubId(eventDto.getHubId())
                .setTimestamp(eventDto.getTimestamp().toEpochMilli());

        // Явно указываем тип для union вместо общего Object
        switch (eventDto.getType()) {
            case CLIMATE_SENSOR_EVENT:
                ClimateSensorEventDto climateDto = (ClimateSensorEventDto) eventDto;
                ClimateSensorEventAvro climateEvent = ClimateSensorEventAvro.newBuilder()
                        .setTemperatureC(climateDto.getTemperatureC())
                        .setHumidity(climateDto.getHumidity())
                        .setCo2Level(climateDto.getCo2Level())
                        .build();
                sensorEventBuilder.setPayload(climateEvent);
                break;

            case LIGHT_SENSOR_EVENT:
                LightSensorEventDto lightDto = (LightSensorEventDto) eventDto;
                LightSensorEventAvro lightEvent = LightSensorEventAvro.newBuilder()
                        .setLinkQuality(lightDto.getLinkQuality())
                        .setLuminosity(lightDto.getLuminosity())
                        .build();
                sensorEventBuilder.setPayload(lightEvent);
                break;

            case MOTION_SENSOR_EVENT:
                MotionSensorEventDto motionDto = (MotionSensorEventDto) eventDto;
                MotionSensorEventAvro motionEvent = MotionSensorEventAvro.newBuilder()
                        .setLinkQuality(motionDto.getLinkQuality())
                        .setMotion(motionDto.getMotion())
                        .setVoltage(motionDto.getVoltage())
                        .build();
                sensorEventBuilder.setPayload(motionEvent);
                break;

            case SWITCH_SENSOR_EVENT:
                SwitchSensorEventDto switchDto = (SwitchSensorEventDto) eventDto;
                SwitchSensorEventAvro switchEvent = SwitchSensorEventAvro.newBuilder()
                        .setState(switchDto.getState())
                        .build();
                sensorEventBuilder.setPayload(switchEvent);
                break;

            case TEMPERATURE_SENSOR_EVENT:
                TemperatureSensorEventDto tempDto = (TemperatureSensorEventDto) eventDto;
                TemperatureSensorEventAvro tempEvent = TemperatureSensorEventAvro.newBuilder()
                        .setTemperatureC(tempDto.getTemperatureC())
                        .setTemperatureF(tempDto.getTemperatureF())
                        .build();
                sensorEventBuilder.setPayload(tempEvent);
                break;

            default:
                throw new IllegalArgumentException("Unknown sensor event type: " + eventDto.getType());
        }

        return sensorEventBuilder.build();
    }
}
