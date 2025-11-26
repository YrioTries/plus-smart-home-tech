package ru.yandex.practicum.grpc.converter.hub;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;

@Component
public class ScenarioAddedToAvroConverter {

    public HubEvent convertToAvro(ru.yandex.practicum.grpc.telemetry.messages.HubEventProto proto) {
        Timestamp protoTimestamp = proto.getTimestamp();
        //Instant instant = Instant.ofEpochSecond(protoTimestamp.getSeconds(), protoTimestamp.getNanos());

        ScenarioAddedEvent scenarioAddedEventAvro = finalConvert(proto.getScenarioAdded());

        return HubEvent.newBuilder()
                .setHubId(proto.getHubId())
                .setTimestamp(protoTimestamp.getSeconds() + protoTimestamp.getNanos())
                .setPayload(scenarioAddedEventAvro)
                .build();
    }

    private ScenarioAddedEvent finalConvert(ru.yandex.practicum.grpc.telemetry.messages.ScenarioAddedEventProto protoPayload) {
        return ScenarioAddedEvent.newBuilder()
                .setName(protoPayload.getName())
                .setActions(convertActions(protoPayload))
                .setConditions(convertConditions(protoPayload))
                .build();
    }

    private List<DeviceAction> convertActions(ru.yandex.practicum.grpc.telemetry.messages.ScenarioAddedEventProto proto) {
        return proto.getActionList().stream()
                .map(this::convertDeviceAction)
                .toList();
    }

    private DeviceAction convertDeviceAction(ru.yandex.practicum.grpc.telemetry.messages.DeviceActionProto action) {
        return DeviceAction.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(convertActionType(action.getType()))
                .setValue(action.getValue())
                .build();
    }

    private ActionType convertActionType(ru.yandex.practicum.grpc.telemetry.messages.ActionTypeProto actionType) {
        return switch (actionType) {
            case ACTIVATE -> ActionType.ACTIVATE;
            case DEACTIVATE -> ActionType.DEACTIVATE;
            case INVERSE -> ActionType.INVERSE;
            case SET_VALUE -> ActionType.SET_VALUE;
            default -> throw new IndexOutOfBoundsException("Action type " + actionType + " not found");
        };
    }

    private List<ScenarioCondition> convertConditions(ru.yandex.practicum.grpc.telemetry.messages.ScenarioAddedEventProto proto) {
        return proto.getConditionList().stream()
                .map(this::convertCondition)
                .toList();
    }

    private ScenarioCondition convertCondition(ru.yandex.practicum.grpc.telemetry.messages.ScenarioConditionProto condition) {
        ScenarioCondition.Builder builder = ScenarioCondition.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(convertConditionType(condition.getType()))
                .setOperation(convertConditionOperation(condition.getOperation()));

        switch (condition.getValueCase()) {
            case BOOL_VALUE -> builder.setValue(condition.getBoolValue());
            case INT_VALUE -> builder.setValue(condition.getIntValue());
        }

        return builder.build();
    }

    private ConditionType convertConditionType(ru.yandex.practicum.grpc.telemetry.messages.ConditionTypeProto conditionType) {
        return switch (conditionType) {
            case MOTION -> ConditionType.MOTION;
            case LUMINOSITY -> ConditionType.LUMINOSITY;
            case SWITCH -> ConditionType.SWITCH;
            case TEMPERATURE -> ConditionType.TEMPERATURE;
            case CO2LEVEL -> ConditionType.CO2LEVEL;
            case HUMIDITY -> ConditionType.HUMIDITY;
            default -> throw new IndexOutOfBoundsException("Condition type " + conditionType + " not found");
        };
    }

    private ConditionOperation convertConditionOperation(ru.yandex.practicum.grpc.telemetry.messages.ConditionOperationProto operation) {
        return switch (operation) {
            case EQUALS -> ConditionOperation.EQUALS;
            case GREATER_THAN -> ConditionOperation.GREATER_THAN;
            case LOWER_THAN -> ConditionOperation.LOWER_THAN;
            default -> throw new IndexOutOfBoundsException("Operation type " + operation + " not found");
        };
    }
}
