package ru.yandex.practicum.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.entity.*;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.repository.*;

@Service
@RequiredArgsConstructor
public class HubEventTxService {

    private final ActionRepository actionRepository;
    private final ConditionRepository conditionRepository;
    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;

    @Transactional
    public void saveDevice(String sensorId, String hubId) {
        Sensor sensor = Sensor.builder()
                .id(sensorId)
                .hubId(hubId)
                .build();
        sensorRepository.save(sensor);
    }

    @Transactional
    public void saveScenario(HubEventAvro event, ScenarioAddedEventAvro added) {
        Scenario newScenario = Scenario.builder()
                .hubId(event.getHubId())
                .name(added.getName())
                .build();

        Scenario savedScenario = scenarioRepository.save(newScenario);

        added.getConditions().forEach(condition -> {
            Integer value = switch (condition.getType().toString()) {
                case "MOTION", "SWITCH" -> 1;  // движение/вкл
                default -> {
                    Object rawValue = condition.getValue();
                    yield (rawValue instanceof Boolean b) ? (b ? 1 : 0)
                            : (rawValue instanceof Integer i) ? i
                            : 0;
                }
            };

            Condition newCondition = Condition.builder()
                    .type(condition.getType().toString())
                    .operation(condition.getOperation().toString())
                    .value(value)
                    .build();

            Condition savedCondition = conditionRepository.save(newCondition);

            Sensor sensor = sensorRepository.findByIdAndHubId(condition.getSensorId(), event.getHubId())
                    .orElseThrow(() -> new EntityNotFoundException("Сенсор " + condition.getSensorId() + " не найден"));

            ScenarioConditionId sensorId = ScenarioConditionId.builder()
                    .scenarioId(savedScenario.getId())
                    .sensorId(sensor.getId())
                    .conditionId(savedCondition.getId())
                    .build();

            ScenarioCondition scenarioCondition = ScenarioCondition.builder()
                    .id(sensorId)
                    .scenario(savedScenario)
                    .sensor(sensor)
                    .condition(savedCondition)
                    .build();

            scenarioConditionRepository.save(scenarioCondition);
        });

        added.getActions().forEach(action -> {
            // Аналогично conditions - обрабатываем Object → Integer
            Integer value = switch (action.getType().toString()) {
                case "ACTIVATE" -> 1;
                case "DEACTIVATE" -> 0;
                default -> {
                    Object rawValue = action.getValue();
                    yield (rawValue instanceof Boolean b) ? (b ? 1 : 0)
                            : (rawValue instanceof Integer i) ? i
                            : 0;  // дефолт для неизвестных
                }
            };

            Action newAction = Action.builder()
                    .type(action.getType().toString())
                    .value(value)
                    .build();

            Action saveAction = actionRepository.save(newAction);

            Sensor sensor = sensorRepository.findByIdAndHubId(action.getSensorId(), event.getHubId())
                    .orElseThrow(() -> new EntityNotFoundException("Сенсор " + action.getSensorId() + " не найден"));

            ScenarioActionId actionId = ScenarioActionId.builder()
                    .scenarioId(savedScenario.getId())
                    .sensorId(sensor.getId())
                    .actionId(saveAction.getId())
                    .build();

            ScenarioAction scenarioAction = ScenarioAction.builder()
                    .id(actionId)
                    .scenario(savedScenario)
                    .sensor(sensor)
                    .action(saveAction)
                    .build();

            scenarioActionRepository.save(scenarioAction);
        });
    }

    @Transactional
    public void removeDevice(String sensorId, String hubId) {
        Sensor sensor = sensorRepository.findByIdAndHubId(sensorId, hubId)
                .orElseThrow(() -> new EntityNotFoundException("Сенсор " + sensorId + " не найден"));
        sensorRepository.delete(sensor);
    }

    @Transactional
    public void removeScenario(String name, String hubId) {
        Scenario scenario = scenarioRepository.findByHubIdAndName(name, hubId)
                .orElseThrow(() -> new EntityNotFoundException("Сценарий " + name + " не найден"));
        scenarioRepository.delete(scenario);
    }
}
