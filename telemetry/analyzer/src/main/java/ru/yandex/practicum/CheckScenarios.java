package ru.yandex.practicum;

import com.google.protobuf.util.Timestamps;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.entity.Scenario;
import ru.yandex.practicum.entity.ScenarioAction;
import ru.yandex.practicum.entity.ScenarioCondition;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.repository.ScenarioActionRepository;
import ru.yandex.practicum.repository.ScenarioConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class CheckScenarios {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final SensorRepository sensorRepository;

    public List<ru.yandex.practicum.grpc.telemetry.messages.DeviceActionRequest> checkScenarios(SensorsSnapshotAvro snapshot) {
        log.info("Начинаю проверку сценариев...");

        List<ru.yandex.practicum.grpc.telemetry.messages.DeviceActionRequest> result = new ArrayList<>();

        List<Scenario> scenarioList = scenarioRepository.findByHubId(snapshot.getHubId());
        if (scenarioList.isEmpty()) {
            return result;
        }

        List<Long> scenarioIds = scenarioList.stream()
                .map(Scenario::getId)
                .toList();

        List<ScenarioCondition> allConditions =
                scenarioConditionRepository.findAllByScenarioIdIn(scenarioIds);
        List<ScenarioAction> allActions =
                scenarioActionRepository.findAllByScenarioIdIn(scenarioIds);

        Map<Long, List<ScenarioCondition>> conditionsByScenario = allConditions.stream()
                .collect(Collectors.groupingBy(sc -> sc.getScenario().getId()));
        Map<Long, List<ScenarioAction>> actionsByScenario = allActions.stream()
                .collect(Collectors.groupingBy(sa -> sa.getScenario().getId()));

        for (Scenario scenario : scenarioList) {
            List<ScenarioCondition> scenarioConditions =
                    conditionsByScenario.getOrDefault(scenario.getId(), List.of());

            boolean allConditionsTrue = scenarioConditions.stream()
                    .allMatch(condition -> checkCondition(condition, snapshot, snapshot.getHubId()));

            if (allConditionsTrue) {
                log.info("Все условия прошли проверку!");
                List<ScenarioAction> actions =
                        actionsByScenario.getOrDefault(scenario.getId(), List.of());

                for (ScenarioAction action : actions) {
                    ru.yandex.practicum.grpc.telemetry.messages.DeviceActionProto deviceActionProto = ru.yandex.practicum.grpc.telemetry.messages.DeviceActionProto.newBuilder()
                            .setSensorId(action.getSensor().getId())
                            .setType(ru.yandex.practicum.grpc.telemetry.messages.ActionTypeProto.valueOf(action.getAction().getType()))
                            .setValue(action.getAction().getValue())
                            .build();

                    ru.yandex.practicum.grpc.telemetry.messages.DeviceActionRequest request = ru.yandex.practicum.grpc.telemetry.messages.DeviceActionRequest.newBuilder()
                            .setHubId(snapshot.getHubId())
                            .setScenarioName(action.getScenario().getName())
                            .setAction(deviceActionProto)
                            .setTimestamp(Timestamps.fromMillis(System.currentTimeMillis()))
                            .build();

                    result.add(request);
                }
            }
        }

        return result;
    }

    private boolean checkCondition(ScenarioCondition condition, SensorsSnapshotAvro snapshot, String hubId) {
        String sensorId = condition.getSensor().getId();

        // Создаем Map из массива sensorsState для удобного поиска
        Map<String, SensorStateAvro> sensorsStateMap = snapshot.getSensorsState().stream()
                .collect(Collectors.toMap(DeviceState::getDeviceId, DeviceState::getState));

        SensorStateAvro state = sensorsStateMap.get(sensorId);
        if (state == null || state.getData() == null) {
            log.info("Данных для сенсора {} пока нет, пропускаем проверку", sensorId);
            return true;
        }

        sensorRepository.findByIdAndHubId(sensorId, hubId)
                .orElseThrow(() -> new EntityNotFoundException("Датчик " + sensorId + " не найден"));

        ConditionOperation operation = ConditionOperation.valueOf(condition.getCondition().getOperation());
        ConditionType deviceType = ConditionType.valueOf(condition.getCondition().getType());

        return switch (deviceType) {
            case MOTION -> {
                MotionSensorEvent data = (MotionSensorEvent) state.getData();
                int actual = data.getMotion() ? 1 : 0;
                yield checkOperation(operation, actual, condition.getCondition().getValue());
            }
            case LUMINOSITY -> {
                LightSensorEvent data = (LightSensorEvent) state.getData();
                int actual = data.getLuminosity();
                yield checkOperation(operation, actual, condition.getCondition().getValue());
            }
            case TEMPERATURE -> {
                ClimateSensorEvent data = (ClimateSensorEvent) state.getData();
                int actual = data.getTemperatureC();
                yield checkOperation(operation, actual, condition.getCondition().getValue());
            }
            case HUMIDITY -> {
                ClimateSensorEvent data = (ClimateSensorEvent) state.getData();
                int actual = data.getHumidity();
                yield checkOperation(operation, actual, condition.getCondition().getValue());
            }
            case CO2LEVEL -> {
                ClimateSensorEvent data = (ClimateSensorEvent) state.getData();
                int actual = data.getCo2Level();
                yield checkOperation(operation, actual, condition.getCondition().getValue());
            }
            case SWITCH -> {
                SwitchSensorEvent data = (SwitchSensorEvent) state.getData();
                int actual = data.getState() ? 1 : 0;
                yield checkOperation(operation, actual, condition.getCondition().getValue());
            }
        };
    }

    private boolean checkOperation(ConditionOperation operationType, Integer expected, Integer actual) {
        return switch (operationType) {
            case EQUALS -> actual.equals(expected);
            case GREATER_THAN -> actual < expected;
            case LOWER_THAN -> actual > expected;
        };
    }
}
