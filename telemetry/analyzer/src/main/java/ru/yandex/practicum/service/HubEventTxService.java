package ru.yandex.practicum.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.entity.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.repository.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
    public Scenario saveScenario(ScenarioAddedEventAvro event, String hubId) {
        //Собираем набор по условиям и действиям
        Set<String> sensors = new HashSet<>();
        event.getConditions().forEach(condition -> sensors.add(condition.getSensorId()));
        event.getActions().forEach(action -> sensors.add(action.getSensorId()));

        //проверяем, что всё есть, можно дальше работать.
        boolean allSensorsExists = sensorRepository.existsByIdInAndHubId(sensors, hubId);
        if(!allSensorsExists) {
            throw new IllegalStateException("Нет возможности создать сценарий с использованием неизвестного устройства");
        }

        //Пытаемся найти уже существующий сценарий.
        //Если нет-создаём, если есть-удаляет старое
        Optional<Scenario> maybeExist = scenarioRepository.findByHubIdAndName(hubId, event.getName());

        Scenario scenario;
        if(maybeExist.isEmpty()) {
            scenario = new Scenario();
            scenario.setName(event.getName());
            scenario.setHubId(hubId);
        } else {
            scenario = maybeExist.get();
            Map<String, Condition> conditions = scenario.getConditions();
            conditionRepository.deleteAll(conditions.values());
            scenario.getConditions().clear();

            Map<String, Action> actions = scenario.getActions();
            actionRepository.deleteAll(actions.values());
            scenario.getActions().clear();
        }

        //Заново пересобираем новые условия и действия.
        for (ScenarioConditionAvro eventCondition : event.getConditions()) {
            Condition condition = new Condition();
            condition.setType(eventCondition.getType().toString());
            condition.setOperation(setStringOperation(eventCondition.getOperation()));
            condition.setValue(mapValue(eventCondition.getValue()));

            scenario.addCondition(eventCondition.getSensorId(), condition);
        }

        for (DeviceActionAvro eventAction : event.getActions()) {
            Action action = new Action();
            action.setType(eventAction.getType().toString());
            if(eventAction.getType().equals(ActionTypeAvro.SET_VALUE)) {
                action.setValue(mapValue(eventAction.getValue()));
            }

            scenario.addAction(eventAction.getSensorId(), action);
        }
        // И только когда уже всё собрано, в самом конце, запоминаем по таблицам
        conditionRepository.saveAll(scenario.getConditions().values());
        actionRepository.saveAll(scenario.getActions().values());
        return scenarioRepository.save(scenario);
    }


    @Transactional
    public void removeDevice(String sensorId, String hubId) {
        Sensor sensor = sensorRepository.findByIdAndHubId(sensorId, hubId)
                .orElseThrow(() -> new EntityNotFoundException("Сенсор " + sensorId + " не найден"));
        sensorRepository.delete(sensor);
    }

    @Transactional
    public void removeScenario(String hubId, String name) {
        Scenario scenario = scenarioRepository.findByHubIdAndName(name, hubId)
                .orElseThrow(() -> new EntityNotFoundException("Сценарий " + name + " не найден"));
        scenarioRepository.delete(scenario);
    }

    private String setStringOperation(ConditionOperationAvro operationType) {
        return switch (operationType) {
            case EQUALS -> "EQUALS";
            case GREATER_THAN -> "GREATER_THAN";
            case LOWER_THAN -> "LOWER_THAN";
        };
    }

    private Integer mapValue(Object rawValue) {
        return switch (rawValue) {
            case Boolean b -> b ? 1 : 0;
            case Integer i -> i;
            case null -> 0;
            default -> 0;
        };
    }
}
