package ru.yandex.practicum.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.entity.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioCondition;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

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
    public void saveScenario(HubEvent event, ScenarioAddedEvent added) {
        Scenario newScenario = Scenario.builder()
                .hubId(event.getHubId())
                .name(added.getName())
                .build();

        Scenario savedScenario = scenarioRepository.save(newScenario);

        added.getConditions().forEach(condition -> {
            Integer value = null;
            if (condition.getValue() instanceof Boolean boolVal) {
                value = boolVal ? 1 : 0;
            } else if (condition.getValue() instanceof Integer intVal) {
                value = intVal;
            }

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
            Action newAction = Action.builder()
                    .type(action.getType().toString())
                    .value(action.getValue())
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
