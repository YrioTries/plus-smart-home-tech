package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.entity.Condition;
import ru.yandex.practicum.entity.Scenario;
import ru.yandex.practicum.entity.ScenarioAction;
import ru.yandex.practicum.entity.ScenarioCondition;
import ru.yandex.practicum.kafka.telemetry.event.ConditionType;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceState;
import ru.yandex.practicum.model.DeviceStateData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotProcessingService {

    private final ScenarioService scenarioService;
    private final ActionExecutionService actionExecutionService;

    public void processSnapshot(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        log.debug("Processing snapshot for hub: {}", hubId);

        List<Scenario> scenarios = scenarioService.getScenariosByHubId(hubId);
        if (scenarios.isEmpty()) {
            log.debug("No scenarios found for hub: {}", hubId);
            return;
        }

        Map<String, DeviceStateData> deviceStates = extractDeviceStates(snapshot);

        for (Scenario scenario : scenarios) {
            if (evaluateScenario(scenario, deviceStates)) {
                log.info("Scenario '{}' conditions met for hub: {}", scenario.getName(), hubId);
                executeScenarioActions(scenario, hubId);
            }
        }
    }

    private Map<String, DeviceStateData> extractDeviceStates(SensorsSnapshotAvro snapshot) {
        return snapshot.getSensorsState().stream()
                .collect(Collectors.toMap(
                        DeviceState::getDeviceId,
                        this::convertToDeviceStateData
                ));
    }

    private DeviceStateData convertToDeviceStateData(DeviceState deviceState) {
        return DeviceStateData.builder()
                .deviceId(deviceState.getDeviceId())
                .timestamp(deviceState.getState().getTimestamp())
                .sensorState(deviceState.getState())
                .build();
    }

    private boolean evaluateScenario(Scenario scenario, Map<String, DeviceStateData> deviceStates) {
        for (ScenarioCondition scenarioCondition : scenario.getConditions()) {
            String sensorId = scenarioCondition.getSensor().getId();
            Condition condition = scenarioCondition.getCondition();
            DeviceStateData deviceState = deviceStates.get(sensorId);

            if (deviceState == null || !evaluateCondition(condition, deviceState)) {
                return false;
            }
        }
        return true;
    }

    private boolean evaluateCondition(Condition condition, DeviceStateData deviceState) {
        Integer sensorValue = extractSensorValue(condition.getType(), deviceState);
        if (sensorValue == null) return false;

        return switch (condition.getOperation()) {
            case EQUALS -> sensorValue.equals(condition.getValue());
            case GREATER_THAN -> sensorValue > condition.getValue();
            case LOWER_THAN -> sensorValue < condition.getValue();
        };
    }

    private Integer extractSensorValue(ConditionType conditionType, DeviceStateData deviceState) {
        var sensorState = deviceState.getSensorState();
        var data = sensorState.getData();

        return switch (conditionType) {
            case TEMPERATURE -> {
                if (data instanceof ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorEvent temp) {
                    yield temp.getTemperatureC();
                } else if (data instanceof ru.yandex.practicum.kafka.telemetry.event.ClimateSensorEvent climate) {
                    yield climate.getTemperatureC();
                }
                yield null;
            }
            case HUMIDITY -> {
                if (data instanceof ru.yandex.practicum.kafka.telemetry.event.ClimateSensorEvent climate) {
                    yield climate.getHumidity();
                }
                yield null;
            }
            case CO2LEVEL -> {
                if (data instanceof ru.yandex.practicum.kafka.telemetry.event.ClimateSensorEvent climate) {
                    yield climate.getCo2Level();
                }
                yield null;
            }
            case LUMINOSITY -> {
                if (data instanceof ru.yandex.practicum.kafka.telemetry.event.LightSensorEvent light) {
                    yield light.getLuminosity();
                }
                yield null;
            }
            case MOTION -> {
                if (data instanceof ru.yandex.practicum.kafka.telemetry.event.MotionSensorEvent motion) {
                    yield motion.getMotion() ? 1 : 0;
                }
                yield null;
            }
            case SWITCH -> {
                if (data instanceof ru.yandex.practicum.kafka.telemetry.event.SwitchSensorEvent switchSensor) {
                    yield switchSensor.getState() ? 1 : 0;
                }
                yield null;
            }
        };
    }

    private void executeScenarioActions(Scenario scenario, String hubId) {
        List<ru.yandex.practicum.grpc.telemetry.messages.DeviceActionProto> actions = new ArrayList<>();

        for (ScenarioAction scenarioAction : scenario.getActions()) {
            var action = scenarioAction.getAction();
            var sensor = scenarioAction.getSensor();

            var actionProto = ru.yandex.practicum.grpc.telemetry.messages.DeviceActionProto.newBuilder()
                    .setSensorId(sensor.getId())
                    .setType(ru.yandex.practicum.grpc.telemetry.messages.ActionTypeProto.valueOf(action.getType().name()))
                    .setValue(action.getValue() != null ? action.getValue() : 0)
                    .build();

            actions.add(actionProto);
        }

        actionExecutionService.executeActions(hubId, scenario.getName(), actions);
    }
}
