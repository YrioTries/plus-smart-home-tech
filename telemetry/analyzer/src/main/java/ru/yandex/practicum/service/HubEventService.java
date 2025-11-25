package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubEventService {

    private final HubEventTxService hubEventTxService;

    public void saveHubEvent(ConsumerRecords<String, HubEvent> hubEventList) {
        hubEventList.forEach(record -> {
            HubEvent event = record.value();
            Object payload = event.getPayload();

            if (payload instanceof DeviceAddedEvent deviceAdded) {
                hubEventTxService.saveDevice(deviceAdded.getId(), event.getHubId());
                log.info("Сенсор {} успешно доабвлен в хаб {}!", deviceAdded.getId(), event.getHubId());
            }

            if (payload instanceof ScenarioAddedEvent scenarioAdded) {
                hubEventTxService.saveScenario(event, scenarioAdded);
                log.info("Сценарий {} успешно добавлен!", scenarioAdded.getName());
            }

            if (payload instanceof DeviceRemovedEvent deviceRemoved) {
                hubEventTxService.removeDevice(deviceRemoved.getId(), event.getHubId());
                log.info("Сенсор {} успешно удален!", deviceRemoved.getId());
            }

            if (payload instanceof ScenarioRemovedEvent scenarioRemoved) {
                hubEventTxService.removeScenario(scenarioRemoved.getName(), event.getHubId());
                log.info("Сценарий {} успешно удален!", scenarioRemoved.getName());
            }
        });
    }
}
