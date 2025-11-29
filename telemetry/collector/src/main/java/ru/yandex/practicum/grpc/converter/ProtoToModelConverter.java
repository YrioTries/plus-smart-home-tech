package ru.yandex.practicum.grpc.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.hub.*;
import ru.yandex.practicum.dto.sensor.*;
import ru.yandex.practicum.grpc.telemetry.messages.*;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ProtoToModelConverter {

    public SensorEventDto convertToModel(SensorEventProto proto) {
        SensorEventProto.PayloadCase payloadCase = proto.getPayloadCase();

        switch (payloadCase) {
            case MOTION_SENSOR:
                return convertMotionSensor(proto);
            case TEMPERATURE_SENSOR:
                return convertTemperatureSensor(proto);
            case LIGHT_SENSOR:
                return convertLightSensor(proto);
            case CLIMATE_SENSOR:
                return convertClimateSensor(proto);
            case SWITCH_SENSOR:
                return convertSwitchSensor(proto);
            case PAYLOAD_NOT_SET:
            default:
                throw new IllegalArgumentException("Неизвестный тип события датчика: " + payloadCase);
        }
    }

    public HubEventDto convertToModel(HubEventProto proto) {
        HubEventProto.PayloadCase payloadCase = proto.getPayloadCase();

        switch (payloadCase) {
            case DEVICE_ADDED:
                return convertDeviceAdded(proto);
            case DEVICE_REMOVED:
                return convertDeviceRemoved(proto);
            case SCENARIO_ADDED:
                return convertScenarioAdded(proto);
            case SCENARIO_REMOVED:
                return convertScenarioRemoved(proto);
            case PAYLOAD_NOT_SET:
            default:
                throw new IllegalArgumentException("Неизвестный тип события хаба: " + payloadCase);
        }
    }

    private MotionSensorEventDto convertMotionSensor(SensorEventProto proto) {
        MotionSensorProto motionSensor = proto.getMotionSensor();
        MotionSensorEventDto event = new MotionSensorEventDto();
        setCommonSensorFields(event, proto);
        event.setLinkQuality(motionSensor.getLinkQuality());
        event.setMotion(motionSensor.getMotion());
        event.setVoltage(motionSensor.getVoltage());
        return event;
    }

    private TemperatureSensorEventDto convertTemperatureSensor(SensorEventProto proto) {
        TemperatureSensorProto tempSensor = proto.getTemperatureSensor();
        TemperatureSensorEventDto event = new TemperatureSensorEventDto();
        setCommonSensorFields(event, proto);
        event.setTemperatureC(tempSensor.getTemperatureC());
        event.setTemperatureF(tempSensor.getTemperatureF());
        return event;
    }

    private LightSensorEventDto convertLightSensor(SensorEventProto proto) {
        LightSensorProto lightSensor = proto.getLightSensor();
        LightSensorEventDto event = new LightSensorEventDto();
        setCommonSensorFields(event, proto);
        event.setLinkQuality(lightSensor.getLinkQuality());
        event.setLuminosity(lightSensor.getLuminosity());
        return event;
    }

    private ClimateSensorEventDto convertClimateSensor(SensorEventProto proto) {
        ClimateSensorProto climateSensor = proto.getClimateSensor();
        ClimateSensorEventDto event = new ClimateSensorEventDto();
        setCommonSensorFields(event, proto);
        event.setTemperatureC(climateSensor.getTemperatureC());
        event.setHumidity(climateSensor.getHumidity());
        event.setCo2Level(climateSensor.getCo2Level());
        return event;
    }

    private SwitchSensorEventDto convertSwitchSensor(SensorEventProto proto) {
        SwitchSensorProto switchSensor = proto.getSwitchSensor();
        SwitchSensorEventDto event = new SwitchSensorEventDto();
        setCommonSensorFields(event, proto);
        event.setState(switchSensor.getState());
        return event;
    }

    private DeviceAddedEventDto convertDeviceAdded(HubEventProto proto) {
        DeviceAddedEventProto deviceAdded = proto.getDeviceAdded();
        DeviceAddedEventDto event = new DeviceAddedEventDto();
        setCommonHubFields(event, proto);
        event.setId(deviceAdded.getId());
        event.setDeviceType(DeviceTypeAvro.valueOf(deviceAdded.getType().name()));
        return event;
    }

    private DeviceRemovedEventDto convertDeviceRemoved(HubEventProto proto) {
        DeviceRemovedEventProto deviceRemoved = proto.getDeviceRemoved();
        DeviceRemovedEventDto event = new DeviceRemovedEventDto();
        setCommonHubFields(event, proto);
        event.setId(deviceRemoved.getId());
        return event;
    }

    private ScenarioAddedEventDto convertScenarioAdded(HubEventProto proto) {
        ScenarioAddedEventProto scenarioAdded = proto.getScenarioAdded();
        ScenarioAddedEventDto event = new ScenarioAddedEventDto();
        setCommonHubFields(event, proto);
        event.setName(scenarioAdded.getName());
        event.setConditions(scenarioAdded.getConditionList().stream()
                .map(this::convertCondition)
                .collect(Collectors.toList()));
        event.setActions(scenarioAdded.getActionList().stream()
                .map(this::convertAction)
                .collect(Collectors.toList()));
        return event;
    }

    private ScenarioRemovedEventDto convertScenarioRemoved(HubEventProto proto) {
        ScenarioRemovedEventProto scenarioRemoved = proto.getScenarioRemoved();
        ScenarioRemovedEventDto event = new ScenarioRemovedEventDto();
        setCommonHubFields(event, proto);
        event.setName(scenarioRemoved.getName());
        return event;
    }

    private ScenarioConditionDto convertCondition(ScenarioConditionProto proto) {
        ScenarioConditionDto condition = new ScenarioConditionDto();
        condition.setSensorId(proto.getSensorId());
        condition.setType(ConditionTypeAvro.valueOf(proto.getType().name()));
        condition.setOperation(ConditionOperationAvro.valueOf(proto.getOperation().name()));

        switch (proto.getValueCase()) {
            case BOOL_VALUE:
                condition.setValue(proto.getBoolValue() ? 1 : 0);
                break;
            case INT_VALUE:
                condition.setValue(proto.getIntValue());
                break;
            case VALUE_NOT_SET:
            default:
                condition.setValue(null);
        }

        return condition;
    }

    private DeviceActionDto convertAction(DeviceActionProto proto) {
        DeviceActionDto action = new DeviceActionDto();
        action.setSensorId(proto.getSensorId());
        action.setType(ActionTypeAvro.valueOf(proto.getType().name()));
        action.setValue(proto.hasValue() ? proto.getValue() : null);
        return action;
    }

    private void setCommonSensorFields(SensorEventDto event, SensorEventProto proto) {
        event.setId(proto.getId());
        event.setHubId(proto.getHubId());
        event.setTimestamp(Instant.ofEpochSecond(
                proto.getTimestamp().getSeconds(),
                proto.getTimestamp().getNanos()
        ));
    }

    private void setCommonHubFields(HubEventDto event, HubEventProto proto) {
        event.setHubId(proto.getHubId());
        event.setTimestamp(Instant.ofEpochSecond(
                proto.getTimestamp().getSeconds(),
                proto.getTimestamp().getNanos()
        ));
    }
}

