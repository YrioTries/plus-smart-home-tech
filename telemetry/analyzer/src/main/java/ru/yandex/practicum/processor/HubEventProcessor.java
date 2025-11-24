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
import ru.yandex.practicum.kafka.telemetry.event.HubEvent;
import ru.yandex.practicum.service.HubEventProcessingService;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final KafkaConfig config;
    private final HubEventProcessingService hubEventProcessingService;

    private volatile boolean running = true;
    private KafkaConsumer<String, HubEvent> consumer;

    @Override
    public void run() {
        log.info("Starting HubEventProcessor in separate thread...");

        consumer = new KafkaConsumer<>(config.getConsumerConfig());
        consumer.subscribe(Collections.singletonList(TopicType.TELEMETRY_HUBS_V1.getTopic()));

        log.info("Subscribed to topic: telemetry.hubs.v1");

        try {
            while (running) {
                ConsumerRecords<String, HubEvent> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, HubEvent> record : records) {
                    try {
                        log.debug("Processing hub event: key={}, offset={}, partition={}",
                                record.key(), record.offset(), record.partition());

                        hubEventProcessingService.processHubEvent(record.value());

                    } catch (Exception e) {
                        log.error("Error processing hub event: key={}, offset={}",
                                record.key(), record.offset(), e);
                    }
                }

                if (!records.isEmpty()) {
                    consumer.commitSync();
                    log.debug("Committed offsets for {} hub event records", records.count());
                }
            }
        } catch (WakeupException e) {
            log.info("HubEventProcessor wakeup called");
        } catch (Exception e) {
            log.error("Unexpected error in HubEventProcessor", e);
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        log.info("Shutting down HubEventProcessor...");
        running = false;
        if (consumer != null) {
            consumer.wakeup();
        }
    }
}
