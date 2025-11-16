package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.InjectionStrategy;
import ru.yandex.practicum.dto.hub.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class HubEventMapper {

    public HubEvent convertHubToAvro(HubEventDto eventDto) {
        HubEvent hubEvent = new HubEvent();
        hubEvent.setHubId(eventDto.getHubId());
        hubEvent.setTimestamp(eventDto.getTimestamp().toEpochMilli());

        // Обработка payload в зависимости от типа события
        switch (eventDto.getType()) {
            case DEVICE_ADDED: {
                DeviceAddedEventDto addedEventDto = (DeviceAddedEventDto) eventDto;
                DeviceAddedEvent addedPayload = new DeviceAddedEvent();
                addedPayload.setId(addedEventDto.getId());
                addedPayload.setDeviceType(addedEventDto.getDeviceType());
                hubEvent.setPayload(addedPayload);
                break;
            }
            case DEVICE_REMOVED: {
                DeviceRemovedEventDto removedEventDto = (DeviceRemovedEventDto) eventDto;
                DeviceRemovedEvent removedPayload = new DeviceRemovedEvent();
                removedPayload.setId(removedEventDto.getId());
                hubEvent.setPayload(removedPayload);
                break;
            }
            case SCENARIO_ADDED: {
                ScenarioAddedEventDto scenarioAddedDto = (ScenarioAddedEventDto) eventDto;
                ScenarioAddedEvent scenarioAddedPayload = new ScenarioAddedEvent();
                scenarioAddedPayload.setName(scenarioAddedDto.getName());

                // Конвертация условий (conditions)
                List<ScenarioCondition> conditions = scenarioAddedDto.getConditions().stream()
                        .map(this::convertScenarioCondition)
                        .toList();
                scenarioAddedPayload.setConditions(conditions);

                // Конвертация действий (actions)
                List<DeviceAction> actions = scenarioAddedDto.getActions().stream()
                        .map(this::convertDeviceAction)
                        .toList();
                scenarioAddedPayload.setActions(actions);

                hubEvent.setPayload(scenarioAddedPayload);
                break;
            }
            case SCENARIO_REMOVED: {
                ScenarioRemovedEventDto scenarioRemovedDto = (ScenarioRemovedEventDto) eventDto;
                ScenarioRemovedEvent scenarioRemovedPayload = new ScenarioRemovedEvent();
                scenarioRemovedPayload.setName(scenarioRemovedDto.getName());
                hubEvent.setPayload(scenarioRemovedPayload);
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown hub event type: " + eventDto.getType());
        }

        return hubEvent;
    }

    // Вспомогательный метод для конвертации ScenarioConditionDto в ScenarioCondition
    private ScenarioCondition convertScenarioCondition(ScenarioConditionDto<?> conditionDto) {
        ScenarioCondition condition = new ScenarioCondition();
        condition.setSensorId(conditionDto.getSensorId());
        condition.setType(conditionDto.getType());
        condition.setOperation(conditionDto.getOperation());

        // Обработка значения value (может быть null, int или boolean)
        Object value = conditionDto.getValue();
        if (value instanceof Integer intValue) {
            condition.setValue(intValue);
        } else if (value instanceof Boolean booleanValue) {
            condition.setValue(booleanValue);
        } else {
            condition.setValue(null);
        }

        return condition;
    }

    // Вспомогательный метод для конвертации DeviceActionDto в DeviceAction
    private DeviceAction convertDeviceAction(DeviceActionDto actionDto) {
        DeviceAction action = new DeviceAction();
        action.setSensorId(actionDto.getSensorId());
        action.setType(actionDto.getType());
        action.setValue(actionDto.getValue());
        return action;
    }
}
