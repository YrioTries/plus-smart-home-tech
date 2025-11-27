package ru.yandex.practicum.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.service.HubEventService;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final HubEventService service;
    private final AnalyzerConsumerConfig consumerConfig;

    @Value("${spring.kafka.topics.hub-topic-name}")
    private String hubEventTopic;

    private volatile boolean running = true;
    private KafkaConsumer<String, HubEventAvro> consumer;

    @Override
    public void run() {
        log.info("Starting HubEventProcessor...");

        try {
            consumer = consumerConfig.createHubEventConsumer();
            consumer.subscribe(Collections.singletonList(hubEventTopic));
            log.info("HubEventProcessor successfully subscribed to topic: {}", hubEventTopic);

            while (running) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(1000));

                if (!records.isEmpty()) {
                    log.info("Processing {} hub event records", records.count());

                    try {
                        service.saveHubEvent(records);
                        log.debug("Successfully processed {} hub events", records.count());
                    } catch (Exception e) {
                        log.error("Error processing hub events batch. Count: {}", records.count(), e);
                    }
                }

                consumer.commitSync();
            }
        } catch (WakeupException e) {
            log.info("HubEventProcessor received wakeup signal");
        } catch (Exception e) {
            log.error("Unexpected error in HubEventProcessor", e);
        } finally {
            if (consumer != null) {
                try {
                    consumer.close();
                    log.info("HubEventProcessor Kafka consumer closed");
                } catch (Exception e) {
                    log.warn("Error closing Kafka consumer", e);
                }
            }
            log.info("HubEventProcessor stopped");
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