package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.config.KafkaConfig;
import ru.yandex.practicum.kafka.config.TopicType;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.service.SnapshotProcessingService;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    private final KafkaConfig config;
    private final SnapshotProcessingService snapshotProcessingService;

    private volatile boolean running = true;
    private KafkaConsumer<String, SensorsSnapshotAvro> consumer;

    public void start() {
        log.info("Starting SnapshotProcessor...");

        consumer = new KafkaConsumer<>(config.getConsumerConfig());
        consumer.subscribe(Collections.singletonList(TopicType.TELEMETRY_SNAPSHOTS_V1.getTopic()));

        log.info("Subscribed to topic: telemetry.snapshots.v1");


        try {
            while (running) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    try {
                        log.debug("Processing snapshot: key={}, offset={}, partition={}",
                                record.key(), record.offset(), record.partition());

                        snapshotProcessingService.processSnapshot(record.value());

                    } catch (Exception e) {
                        log.error("Error processing snapshot: key={}, offset={}",
                                record.key(), record.offset(), e);
                    }
                }

                if (!records.isEmpty()) {
                    consumer.commitSync();
                    log.debug("Committed offsets for {} records", records.count());
                }
            }
        } catch (WakeupException e) {
            log.info("SnapshotProcessor wakeup called");
        } catch (Exception e) {
            log.error("Unexpected error in SnapshotProcessor", e);
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        log.info("Shutting down SnapshotProcessor...");
        running = false;
        if (consumer != null) {
            consumer.wakeup();
        }
    }
}
