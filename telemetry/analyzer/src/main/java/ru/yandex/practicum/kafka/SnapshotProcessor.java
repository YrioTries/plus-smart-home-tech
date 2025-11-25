package ru.yandex.practicum.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.AnalyzerClient;
import ru.yandex.practicum.CheckScenarios;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    private final ConsumerFactory<String, SensorsSnapshotAvro> shapshotConsumerFactory;
    private final CheckScenarios checkScenarios;
    private final AnalyzerClient service;

    @Value("${spring.kafka.topics.snapshots-topic-name}")
    private String snapshotsTopic;

    public void start() {
        try (Consumer<String, SensorsSnapshotAvro> sensorsSnapshotConsumer = shapshotConsumerFactory.createConsumer()) {
            sensorsSnapshotConsumer.subscribe(List.of(snapshotsTopic));

            while(true) {
                var snapshotRecords = sensorsSnapshotConsumer.poll(Duration.ofSeconds(3));
                if (snapshotRecords.count() > 0) {
                    log.info("Получено {} записей", snapshotRecords.count());

                    List<SensorsSnapshotAvro> snapshotList = new ArrayList<>();
                    snapshotRecords.forEach(record -> snapshotList.add(record.value()));
                    snapshotList.forEach(snapshot -> {
                        List<ru.yandex.practicum.grpc.telemetry.messages.DeviceActionRequest> actions = checkScenarios.checkScenarios(snapshot);
                        actions.forEach(service::sendDeviceActions);
                    });
                    snapshotList.clear();
                }

                sensorsSnapshotConsumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка при агрегации событий от датчиков", e);
        }
    }
}
