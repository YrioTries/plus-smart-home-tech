package mapper;

import dto.sensor.*;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.yandex.practicum.kafka.telemetry.event.*;
import java.time.Instant;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class SensorEventMapper {

    public SensorEvent toAvro(SensorEventDto dto) {
        if (dto == null) return null;
        SensorEvent event = new SensorEvent();
        event.setId(dto.getId());
        event.setHubId(dto.getHubId());
        event.setTimestamp(map(dto.getTimestamp()));
        event.setType(dto.getType());

        switch (dto.getType()) {
            case CLIMATE_SENSOR_EVENT:
                event.setPayload(map((ClimateSensorEventDto) dto));
                break;
            case LIGHT_SENSOR_EVENT:
                event.setPayload(map((LightSensorEventDto) dto));
                break;
            case MOTION_SENSOR_EVENT:
                event.setPayload(map((MotionSensorEventDto) dto));
                break;
            case SWITCH_SENSOR_EVENT:
                event.setPayload(map((SwitchSensorEventDto) dto));
                break;
            case TEMPERATURE_SENSOR_EVENT:
                event.setPayload(map((TemperatureSensorEventDto) dto));
                break;
            default:
                throw new IllegalArgumentException("Unknown sensor type: " + dto.getType());
        }
        return event;
    }

    protected long map(Instant value) {
        return value != null ? value.toEpochMilli() : 0L;
    }

    protected ClimateSensorEvent map(ClimateSensorEventDto dto) {
        ClimateSensorEvent event = new ClimateSensorEvent();
        event.setTemperatureC(dto.getTemperatureC());
        event.setHumidity(dto.getHumidity());
        event.setCo2Level(dto.getCo2Level());
        return event;
    }

    protected LightSensorEvent map(LightSensorEventDto dto) {
        LightSensorEvent event = new LightSensorEvent();
        event.setLinkQuality(dto.getLinkQuality());
        event.setLuminosity(dto.getLuminosity());
        return event;
    }

    protected MotionSensorEvent map(MotionSensorEventDto dto) {
        MotionSensorEvent event = new MotionSensorEvent();
        event.setLinkQuality(dto.getLinkQuality());
        event.setMotion(dto.getMotion());
        event.setVoltage(dto.getVoltage());
        return event;
    }

    protected SwitchSensorEvent map(SwitchSensorEventDto dto) {
        SwitchSensorEvent event = new SwitchSensorEvent();
        event.setState(dto.getState());
        return event;
    }

    protected TemperatureSensorEvent map(TemperatureSensorEventDto dto) {
        TemperatureSensorEvent event = new TemperatureSensorEvent();
        event.setTemperatureC(dto.getTemperatureC());
        event.setTemperatureF(dto.getTemperatureF());
        return event;
    }
}
