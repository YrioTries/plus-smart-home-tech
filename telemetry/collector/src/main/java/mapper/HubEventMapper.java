package mapper;

import dto.hub.*;
import org.mapstruct.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface HubEventMapper {

    @SubclassMapping(source = DeviceAddedEventDto.class, target = DeviceAddedEvent.class)
    @SubclassMapping(source = DeviceRemovedEventDto.class, target = DeviceRemovedEvent.class)
    @SubclassMapping(source = ScenarioAddedEventDto.class, target = ScenarioAddedEvent.class)
    @SubclassMapping(source = ScenarioRemovedEventDto.class, target = ScenarioRemovedEvent.class)
    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", expression = "java(dto.getTimestamp().toEpochMilli())")
    HubEvent toAvro(HubEventDto dto);

    @Mapping(target = "sensorId", source = "sensorId")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "operation", source = "operation")
    @Mapping(target = "value", source = "value")
    ScenarioCondition toScenarioCondition(ScenarioConditionDto dto);

    @Mapping(target = "sensorId", source = "sensorId")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "value", source = "value")
    DeviceAction toDeviceAction(DeviceActionDto dto);
}
