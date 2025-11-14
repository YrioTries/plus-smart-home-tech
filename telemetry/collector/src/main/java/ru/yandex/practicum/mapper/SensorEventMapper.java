package ru.yandex.practicum.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.dto.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import java.time.Instant;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class SensorEventMapper {

    @Mapping(target = "timestamp", expression = "java(eventDto.getTimestamp().toEpochMilli())")
    @Mapping(target = "payload", ignore = true) // Игнорируем автоматический маппинг для payload
    public abstract SensorEvent toAvro(SensorEventDto eventDto);

    @AfterMapping
    protected void setPayload(@MappingTarget SensorEvent sensorEvent, SensorEventDto eventDto) {
        switch (eventDto.getType()) {
            case SWITCH_SENSOR_EVENT: {
                SwitchSensorEventDto switchDto = (SwitchSensorEventDto) eventDto;
                SwitchSensorEvent switchPayload = new SwitchSensorEvent();
                switchPayload.setState(switchDto.getState());
                sensorEvent.setPayload(switchPayload);
                break;
            }
            case CLIMATE_SENSOR_EVENT: {
                ClimateSensorEventDto climateDto = (ClimateSensorEventDto) eventDto;
                ClimateSensorEvent climatePayload = new ClimateSensorEvent();
                climatePayload.setTemperatureC(climateDto.getTemperatureC());
                climatePayload.setHumidity(climateDto.getHumidity());
                climatePayload.setCo2Level(climateDto.getCo2Level());
                sensorEvent.setPayload(climatePayload);
                break;
            }
            case LIGHT_SENSOR_EVENT: {
                LightSensorEventDto lightDto = (LightSensorEventDto) eventDto;
                LightSensorEvent lightPayload = new LightSensorEvent();
                lightPayload.setLinkQuality(lightDto.getLinkQuality());
                lightPayload.setLuminosity(lightDto.getLuminosity());
                sensorEvent.setPayload(lightPayload);
                break;
            }
            case MOTION_SENSOR_EVENT: {
                MotionSensorEventDto motionDto = (MotionSensorEventDto) eventDto;
                MotionSensorEvent motionPayload = new MotionSensorEvent();
                motionPayload.setLinkQuality(motionDto.getLinkQuality());
                motionPayload.setMotion(motionDto.getMotion());
                motionPayload.setVoltage(motionDto.getVoltage());
                sensorEvent.setPayload(motionPayload);
                break;
            }
            case TEMPERATURE_SENSOR_EVENT: {
                TemperatureSensorEventDto tempDto = (TemperatureSensorEventDto) eventDto;
                TemperatureSensorEvent tempPayload = new TemperatureSensorEvent();
                tempPayload.setTemperatureC(tempDto.getTemperatureC());
                tempPayload.setTemperatureF(tempDto.getTemperatureF());
                sensorEvent.setPayload(tempPayload);
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown sensor event type: " + eventDto.getType());
        }
    }
}

