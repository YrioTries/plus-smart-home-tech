package ru.yandex.practicum.grpc.converter.hub.processing;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.messages.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;

@Component
public class ScenarioAddedToAvroConverter {

    public HubEventAvro convertToAvro(HubEventProto proto) {
        Timestamp protoTimestamp = proto.getTimestamp();
        //Instant instant = Instant.ofEpochSecond(protoTimestamp.getSeconds(), protoTimestamp.getNanos());

        ScenarioAddedEventAvro scenarioAddedEventAvro = finalConvert(proto.getScenarioAdded());

        return HubEventAvro.newBuilder()
                .setHubId(proto.getHubId())
                .setTimestamp(protoTimestamp.getSeconds() + protoTimestamp.getNanos())
                .setPayload(scenarioAddedEventAvro)
                .build();
    }

    private ScenarioAddedEventAvro finalConvert(ScenarioAddedEventProto protoPayload) {
        return ScenarioAddedEventAvro.newBuilder()
                .setName(protoPayload.getName())
                .setActions(convertActions(protoPayload))
                .setConditions(convertConditions(protoPayload))
                .build();
    }

    private List<DeviceActionAvro> convertActions(ScenarioAddedEventProto proto) {
        return proto.getActionList().stream()
                .map(this::convertDeviceAction)
                .toList();
    }

    private DeviceActionAvro convertDeviceAction(DeviceActionProto action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(convertActionType(action.getType()))
                .setValue(action.getValue())
                .build();
    }

    private ActionTypeAvro convertActionType(ActionTypeProto actionType) {
        return switch (actionType) {
            case ACTIVATE -> ActionTypeAvro.ACTIVATE;
            case DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
            case INVERSE -> ActionTypeAvro.INVERSE;
            case SET_VALUE -> ActionTypeAvro.SET_VALUE;
            default -> throw new IndexOutOfBoundsException("Action type " + actionType + " not found");
        };
    }

    private List<ScenarioConditionAvro> convertConditions(ScenarioAddedEventProto proto) {
        return proto.getConditionList().stream()
                .map(this::convertCondition)
                .toList();
    }

    private ScenarioConditionAvro convertCondition(ScenarioConditionProto condition) {
        ScenarioConditionAvro.Builder builder = ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(convertConditionType(condition.getType()))
                .setOperation(convertConditionOperation(condition.getOperation()));

        switch (condition.getValueCase()) {
            case BOOL_VALUE -> builder.setValue(condition.getBoolValue());
            case INT_VALUE -> builder.setValue(condition.getIntValue());
        }

        return builder.build();
    }

    private ConditionTypeAvro convertConditionType(ConditionTypeProto conditionType) {
        return switch (conditionType) {
            case MOTION -> ConditionTypeAvro.MOTION;
            case LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
            case SWITCH -> ConditionTypeAvro.SWITCH;
            case TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
            case CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
            case HUMIDITY -> ConditionTypeAvro.HUMIDITY;
            default -> throw new IndexOutOfBoundsException("Condition type " + conditionType + " not found");
        };
    }

    private ConditionOperationAvro convertConditionOperation(ConditionOperationProto operation) {
        return switch (operation) {
            case EQUALS -> ConditionOperationAvro.EQUALS;
            case GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
            case LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
            default -> throw new IndexOutOfBoundsException("Operation type " + operation + " not found");
        };
    }
}
