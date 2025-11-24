package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.entity.Scenario;
import ru.yandex.practicum.entity.Sensor;
import ru.yandex.practicum.kafka.telemetry.event.DeviceType;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;


import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioService {
    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;


    public List<Scenario> getScenariosByHubId(String hubId) {
        return scenarioRepository.findByHubIdWithDetails(hubId);
    }


    @Transactional
    public void addScenario(Scenario scenario) {
        scenarioRepository.save(scenario);
        log.info("Added scenario: {} for hub: {}", scenario.getName(), scenario.getHubId());
    }


    @Transactional
    public void removeScenario(String hubId, String scenarioName) {
        Optional<Scenario> scenario = scenarioRepository.findByHubIdAndName(hubId, scenarioName);
        scenario.ifPresent(scenarioRepository::delete);
        log.info("Removed scenario: {} for hub: {}", scenarioName, hubId);
    }


    @Transactional
    public void addDeviceToHub(String hubId, String deviceId, DeviceType deviceType) {
        Sensor sensor = new Sensor();
        sensor.setId(deviceId);
        sensor.setHubId(hubId);
        sensor.setDeviceType(deviceType);
        sensorRepository.save(sensor);
        log.info("Added device: {} of type: {} to hub: {}", deviceId, deviceType, hubId);
    }


    @Transactional
    public void removeDeviceFromHub(String hubId, String deviceId) {
        sensorRepository.deleteByHubIdAndId(hubId, deviceId);
        log.info("Removed device: {} from hub: {}", deviceId, hubId);
    }
}
