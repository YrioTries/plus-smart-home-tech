package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.hub.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HubEventMapper {

    public HubEvent toAvro(HubEventDto eventDto) {
        HubEvent.Builder hubEventBuilder = HubEvent.newBuilder()
                .setHubId(eventDto.getHubId())
                .setTimestamp(eventDto.getTimestamp().toEpochMilli());

        Object payload = createHubPayload(eventDto);
        hubEventBuilder.setPayload(payload);

        return hubEventBuilder.build();
    }

    private Object createHubPayload(HubEventDto eventDto) {
        switch (eventDto.getType()) {
            case DEVICE_ADDED: {
                DeviceAddedEventDto addedEventDto = (DeviceAddedEventDto) eventDto;
                return DeviceAddedEvent.newBuilder()
                        .setId(addedEventDto.getId())
                        .setDeviceType(addedEventDto.getDeviceType())
                        .build();
            }
            case DEVICE_REMOVED: {
                DeviceRemovedEventDto removedEventDto = (DeviceRemovedEventDto) eventDto;
                return DeviceRemovedEvent.newBuilder()
                        .setId(removedEventDto.getId())
                        .build();
            }
            case SCENARIO_ADDED: {
                ScenarioAddedEventDto scenarioAddedDto = (ScenarioAddedEventDto) eventDto;

                List<ScenarioCondition> conditions = scenarioAddedDto.getConditions().stream()
                        .map(this::convertScenarioCondition)
                        .collect(Collectors.toList());

                List<DeviceAction> actions = scenarioAddedDto.getActions().stream()
                        .map(this::convertDeviceAction)
                        .collect(Collectors.toList());

                return ScenarioAddedEvent.newBuilder()
                        .setName(scenarioAddedDto.getName())
                        .setConditions(conditions)
                        .setActions(actions)
                        .build();
            }
            case SCENARIO_REMOVED: {
                ScenarioRemovedEventDto scenarioRemovedDto = (ScenarioRemovedEventDto) eventDto;
                return ScenarioRemovedEvent.newBuilder()
                        .setName(scenarioRemovedDto.getName())
                        .build();
            }
            default:
                throw new IllegalArgumentException("Unknown hub event type: " + eventDto.getType());
        }
    }

    private ScenarioCondition convertScenarioCondition(ScenarioConditionDto<?> conditionDto) {
        ScenarioCondition.Builder builder = ScenarioCondition.newBuilder()
                .setSensorId(conditionDto.getSensorId())
                .setType(conditionDto.getType())
                .setOperation(conditionDto.getOperation());

        Object value = conditionDto.getValue();
        if (value instanceof Integer) {
            builder.setValue((Integer) value);
        } else if (value instanceof Boolean) {
            builder.setValue((Boolean) value);
        }
        // Для null оставляем значение по умолчанию

        return builder.build();
    }

    private DeviceAction convertDeviceAction(DeviceActionDto actionDto) {
        DeviceAction.Builder builder = DeviceAction.newBuilder()
                .setSensorId(actionDto.getSensorId())
                .setType(actionDto.getType());

        if (actionDto.getValue() != null) {
            builder.setValue(actionDto.getValue());
        }

        return builder.build();
    }
}