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

    @Override
    public void run() {
        KafkaConsumer<String, HubEventAvro> consumer = null;
        try {
            consumer = consumerConfig.createHubEventConsumer();
            consumer.subscribe(Collections.singletonList(hubEventTopic));

            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(100));
                log.debug("Получено {} записей", records.count());

                service.saveHubEvent(records);

                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
            // Игнорируем при shutdown
        } catch (Exception e) {
            log.error("Ошибка при обработке событий Kafka", e);
        } finally {
            if (consumer != null) {
                consumer.close();
            }
        }
    }

    public void shutdown() {
        // Будет вызван извне для остановки
    }
}