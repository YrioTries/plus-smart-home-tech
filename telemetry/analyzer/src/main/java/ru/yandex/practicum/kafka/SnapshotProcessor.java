package ru.yandex.practicum.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.CheckScenarios;
import ru.yandex.practicum.grpc.AnalyzerClient;
import ru.yandex.practicum.grpc.telemetry.messages.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {

    private final AnalyzerConsumerConfig consumerConfig;
    private final CheckScenarios checkScenarios;
    private final AnalyzerClient service;

    @Value("${spring.kafka.topics.snapshots-topic-name}")
    private String snapshotsTopic;

    private volatile boolean running = true;

    @Override
    public void run() {
        KafkaConsumer<String, SensorsSnapshotAvro> consumer = null;
        try {
            consumer = consumerConfig.createSensorsSnapshotConsumer();
            consumer.subscribe(Collections.singletonList(snapshotsTopic));

            while (running) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(Duration.ofSeconds(3));

                if (!records.isEmpty()) {
                    log.info("Получено {} записей", records.count());

                    for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                        SensorsSnapshotAvro snapshot = record.value();
                        List<DeviceActionRequest> actions = checkScenarios.checkScenarios(snapshot);
                        actions.forEach(service::sendDeviceActions);
                    }
                }

                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
            // Игнорируем при shutdown
        } catch (Exception e) {
            log.error("Ошибка при агрегации событий от датчиков", e);
        } finally {
            if (consumer != null) {
                consumer.close();
            }
        }
    }

    public void start() {
        new Thread(this).start();
    }

    public void shutdown() {
        running = false;
    }
}
