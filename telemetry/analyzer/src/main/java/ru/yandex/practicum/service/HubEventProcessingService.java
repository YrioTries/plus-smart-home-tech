package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.entity.Scenario;
import ru.yandex.practicum.kafka.telemetry.event.DeviceType;
import ru.yandex.practicum.kafka.telemetry.event.HubEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubEventProcessingService {

    private final ScenarioService scenarioService;

    public void processHubEvent(HubEvent hubEvent) {
        String hubId = hubEvent.getHubId();
        var payload = hubEvent.getPayload();

        try {
            switch (payload) {
                case ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEvent deviceAdded ->
                        handleDeviceAdded(hubId, deviceAdded);
                case ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEvent deviceRemoved ->
                        handleDeviceRemoved(hubId, deviceRemoved);
                case ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEvent scenarioAdded ->
                        handleScenarioAdded(hubId, scenarioAdded);
                case ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEvent scenarioRemoved ->
                        handleScenarioRemoved(hubId, scenarioRemoved);
                default -> log.warn("Unknown hub event type for hub: {}", hubId);
            }
        } catch (Exception e) {
            log.error("Error processing hub event for hub: {}", hubId, e);
        }
    }

    private void handleDeviceAdded(String hubId, ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEvent event) {
        DeviceType deviceType = DeviceType.valueOf(event.getDeviceType().name());
        scenarioService.addDeviceToHub(hubId, event.getId(), deviceType);
    }

    private void handleDeviceRemoved(String hubId, ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEvent event) {
        scenarioService.removeDeviceFromHub(hubId, event.getId());
    }

    private void handleScenarioAdded(String hubId, ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEvent event) {
        Scenario scenario = new Scenario();
        scenario.setHubId(hubId);
        scenario.setName(event.getName());

        scenarioService.addScenario(scenario);
    }

    private void handleScenarioRemoved(String hubId, ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEvent event) {
        scenarioService.removeScenario(hubId, event.getName());
    }
}
