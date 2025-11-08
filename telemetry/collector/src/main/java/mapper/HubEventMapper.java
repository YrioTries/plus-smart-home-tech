package mapper;

import dto.hub.*;
import org.mapstruct.Mapper;
import org.mapstruct.InjectionStrategy;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class HubEventMapper {

    public HubEvent toAvro(HubEventDto dto) {
        if (dto == null) return null;

        HubEvent event = new HubEvent();
        event.setHubId(dto.getHubId());
        event.setTimestamp(dto.getTimestamp() != null ? dto.getTimestamp().toEpochMilli() : 0);

        switch (dto.getType()) {
            case DEVICE_ADDED:
                event.setPayload(map((DeviceAddedEventDto) dto));
                event.setType(HubEventType.DEVICE_ADDED);
                break;
            case DEVICE_REMOVED:
                event.setPayload(map((DeviceRemovedEventDto) dto));
                event.setType(HubEventType.DEVICE_REMOVED);
                break;
            case SCENARIO_ADDED:
                event.setPayload(map((ScenarioAddedEventDto) dto));
                event.setType(HubEventType.SCENARIO_ADDED);
                break;
            case SCENARIO_REMOVED:
                event.setPayload(map((ScenarioRemovedEventDto) dto));
                event.setType(HubEventType.SCENARIO_REMOVED);
                break;
            default:
                throw new IllegalArgumentException("Unknown HubEvent type: " + dto.getType());
        }
        return event;
    }

    protected abstract DeviceAddedEvent map(DeviceAddedEventDto dto);

    protected abstract DeviceRemovedEvent map(DeviceRemovedEventDto dto);

    protected abstract ScenarioAddedEvent map(ScenarioAddedEventDto dto);

    protected abstract ScenarioRemovedEvent map(ScenarioRemovedEventDto dto);

    protected abstract ScenarioCondition map(ScenarioConditionDto dto);
    protected abstract DeviceAction map(DeviceActionDto dto);
}
