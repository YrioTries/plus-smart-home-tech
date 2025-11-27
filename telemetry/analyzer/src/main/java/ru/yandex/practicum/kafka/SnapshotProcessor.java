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
    private final AnalyzerClient analyzerClient;

    @Value("${spring.kafka.topics.snapshots-topic-name}")
    private String snapshotsTopic;

    private volatile boolean running = true;
    private KafkaConsumer<String, SensorsSnapshotAvro> consumer;

    @Override
    public void run() {
        try {
            consumer = consumerConfig.createSensorsSnapshotConsumer();
            consumer.subscribe(Collections.singletonList(snapshotsTopic));
            log.info("SnapshotProcessor started. Subscribed to topic: {}", snapshotsTopic);

            while (running) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(Duration.ofSeconds(3));

                if (!records.isEmpty()) {
                    log.info("Received {} snapshot records", records.count());

                    for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                        try {
                            SensorsSnapshotAvro snapshot = record.value();
                            log.debug("Processing snapshot for hub: {}", snapshot.getHubId());

                            List<DeviceActionRequest> actions = checkScenarios.checkScenarios(snapshot);
                            log.info("Found {} actions to execute for hub: {}", actions.size(), snapshot.getHubId());

                            for (DeviceActionRequest action : actions) {
                                analyzerClient.sendDeviceActions(action);
                            }
                        } catch (Exception e) {
                            log.error("Error processing snapshot: {}", record.value(), e);
                        }
                    }
                }

                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
            log.info("SnapshotProcessor wakeup called");
        } catch (Exception e) {
            log.error("Error in SnapshotProcessor", e);
        } finally {
            if (consumer != null) {
                consumer.close();
                log.info("SnapshotProcessor consumer closed");
            }
        }
    }

    public void shutdown() {
        running = false;
        if (consumer != null) {
            consumer.wakeup();
        }
    }
}