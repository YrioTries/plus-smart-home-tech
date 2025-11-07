package mapper;

import dto.sensor.*;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassMapping;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SensorEventMapper {

    @SubclassMapping(source = ClimateSensorEventDto.class, target = ClimateSensorEvent.class)
    @SubclassMapping(source = LightSensorEventDto.class, target = LightSensorEvent.class)
    @SubclassMapping(source = MotionSensorEventDto.class, target = MotionSensorEvent.class)
    @SubclassMapping(source = SwitchSensorEventDto.class, target = SwitchSensorEvent.class)
    @SubclassMapping(source = TemperatureSensorEventDto.class, target = TemperatureSensorEvent.class)

    @Mapping(target = "id", source = "id")
    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", source = "timestamp")
    @Mapping(target = "type", source = "type")
    Object toAvro(SensorEventDto dto);
}