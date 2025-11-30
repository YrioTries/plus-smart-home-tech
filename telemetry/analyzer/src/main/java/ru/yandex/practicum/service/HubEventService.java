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

    public void saveHubEvent(ConsumerRecords<String, HubEventAvro> hubEventList) {
        hubEventList.forEach(record -> {
            HubEventAvro event = record.value();
            Object payload = event.getPayload();

            if (payload instanceof DeviceAddedEventAvro deviceAdded) {
                hubEventTxService.saveDevice(deviceAdded.getId(), event.getHubId());
                log.info("Сенсор {} успешно доабвлен в хаб {}!", deviceAdded.getId(), event.getHubId());
            }

            if (payload instanceof ScenarioAddedEventAvro scenarioAdded) {
                hubEventTxService.saveScenario(scenarioAdded, event.getHubId());
                log.info("Сценарий {} успешно добавлен!", scenarioAdded.getName());
            }

            if (payload instanceof DeviceRemovedEventAvro deviceRemoved) {
                hubEventTxService.removeDevice(deviceRemoved.getId(), event.getHubId());
                log.info("Сенсор {} успешно удален!", deviceRemoved.getId());
            }

            if (payload instanceof ScenarioRemovedEventAvro scenarioRemoved) {
                hubEventTxService.removeScenario(scenarioRemoved.getName(), event.getHubId());
                log.info("Сценарий {} успешно удален!", scenarioRemoved.getName());
            }
        });
    }
}
