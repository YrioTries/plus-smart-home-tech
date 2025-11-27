package ru.yandex.practicum;

import com.google.protobuf.util.Timestamps;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.entity.Scenario;
import ru.yandex.practicum.entity.ScenarioAction;
import ru.yandex.practicum.entity.ScenarioCondition;
import ru.yandex.practicum.grpc.telemetry.messages.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.messages.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.messages.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.repository.ScenarioActionRepository;
import ru.yandex.practicum.repository.ScenarioConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckScenarios {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final SensorRepository sensorRepository;

    public List<DeviceActionRequest> checkScenarios(SensorsSnapshotAvro snapshot) {
        log.info("Начинаю проверку сценариев...");

        List<DeviceActionRequest> result = new ArrayList<>();

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
                    DeviceActionProto deviceActionProto = DeviceActionProto.newBuilder()
                            .setSensorId(action.getSensor().getId())
                            .setType(ActionTypeProto.valueOf(action.getAction().getType()))
                            .setValue(action.getAction().getValue())
                            .build();

                    DeviceActionRequest request = DeviceActionRequest.newBuilder()
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

        SensorStateAvro state = snapshot.getSensorsState().get(sensorId);
        if (state == null || state.getData() == null) {
            log.info("Данных для сенсора {} пока нет, пропускаем проверку", sensorId);
            return true;
        }

        sensorRepository.findByIdAndHubId(sensorId, hubId)
                .orElseThrow(() -> new EntityNotFoundException("Датчик " + sensorId + " не найден"));

        ConditionOperationAvro operation = ConditionOperationAvro.valueOf(condition.getCondition().getOperation());

        ConditionTypeAvro deviceType = ConditionTypeAvro.valueOf(condition.getCondition().getType());

        return switch (deviceType) {
            case MOTION -> {
                MotionSensorEventAvro data = (MotionSensorEventAvro) state.getData();
                int actual = data.getMotion() ? 1 : 0;
                yield checkOperation(operation, actual, condition.getCondition().getValue());
            }
            case LUMINOSITY -> {
                LightSensorEventAvro data = (LightSensorEventAvro) state.getData();
                int actual = data.getLuminosity();
                yield checkOperation(operation, actual, condition.getCondition().getValue());
            }
            case TEMPERATURE -> {
                ClimateSensorEventAvro data = (ClimateSensorEventAvro) state.getData();
                int actual = data.getTemperatureC();
                yield checkOperation(operation, actual, condition.getCondition().getValue());
            }
            case HUMIDITY -> {
                ClimateSensorEventAvro data = (ClimateSensorEventAvro) state.getData();
                int actual = data.getHumidity();
                yield checkOperation(operation, actual, condition.getCondition().getValue());
            }
            case CO2LEVEL -> {
                ClimateSensorEventAvro data = (ClimateSensorEventAvro) state.getData();
                int actual = data.getCo2Level();
                yield checkOperation(operation, actual, condition.getCondition().getValue());
            }
            case SWITCH -> {
                SwitchSensorEventAvro data = (SwitchSensorEventAvro) state.getData();
                int actual = data.getState() ? 1 : 0;
                yield checkOperation(operation, actual, condition.getCondition().getValue());
            }
        };
    }

    private boolean checkOperation(ConditionOperationAvro operationType, Integer expected, Integer actual) {
        return switch (operationType) {
            case EQUALS -> actual.equals(expected);
            case GREATER_THAN -> actual > expected;
            case LOWER_THAN -> actual < expected;
        };
    }
}
