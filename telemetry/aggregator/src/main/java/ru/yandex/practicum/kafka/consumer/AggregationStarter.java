package ru.yandex.practicum.kafka.consumer;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.config.KafkaConsumerConfig;
import ru.yandex.practicum.kafka.producer.SnapshotProducer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter implements Runnable {

    private final KafkaConsumerConfig consumerConfig;
    private final SnapshotProducer snapshotProducer;
    private final CheckUpdateState checkUpdateState;

    @Value("${spring.kafka.topics.sensor-topic-name}")
    private String sensorsTopic;

    @Value("${spring.kafka.topics.snapshots-topic-name}")
    private String snapshotsTopic;

    private final AtomicBoolean running = new AtomicBoolean(true);
    private Thread workerThread;

    @PostConstruct
    public void start() {
        workerThread = new Thread(this, "AggregationWorker");
        workerThread.start();
        log.info("Агрегатор запущен");
    }

    @Override
    public void run() {
        try (
                KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer = consumerConfig.createSensorsSnapshotConsumer();
                KafkaConsumer<String, SensorEventAvro> eventConsumer = consumerConfig.createSensorEventConsumer()
        ) {
            snapshotConsumer.subscribe(Collections.singletonList(snapshotsTopic));
            eventConsumer.subscribe(Collections.singletonList(sensorsTopic));

            while (running.get()) {
                pollAndProcessEvents(snapshotConsumer, eventConsumer);
            }
        } catch (WakeupException e) {
            if (running.get()) {
                log.error("WakeupException получен, но флаг running=true", e);
                throw e;
            }
            log.info("WakeupException получен во время shutdown - игнорируем");
        } catch (Exception e) {
            log.error("Ошибка при агрегации событий от датчиков", e);
        } finally {
            closeResources();
        }
    }

    private void pollAndProcessEvents(KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer,
                                      KafkaConsumer<String, SensorEventAvro> eventConsumer) {
        // Обработка снапшотов
        ConsumerRecords<String, SensorsSnapshotAvro> snapshotRecords = snapshotConsumer.poll(Duration.ofMillis(500));
        for (ConsumerRecord<String, SensorsSnapshotAvro> record : snapshotRecords) {
            checkUpdateState.putSnapshot(record.value());
            log.info("Снапшот hubId={} загружен из Kafka", record.value().getHubId());
        }
        snapshotConsumer.commitSync();

        // Обработка событий
        ConsumerRecords<String, SensorEventAvro> eventRecords = eventConsumer.poll(Duration.ofMillis(500));
        for (ConsumerRecord<String, SensorEventAvro> record : eventRecords) {
            SensorEventAvro event = record.value();
            Optional<SensorsSnapshotAvro> updatedSnapshot = checkUpdateState.updateState(event);
            updatedSnapshot.ifPresent(snapshot -> {
                try {
                    snapshotProducer.sendSnapshot(snapshot);
                } catch (Exception e) {
                    log.error("Ошибка при отправке события в snapshots.v1: hubId={}", snapshot.getHubId(), e);
                }
            });
        }
        eventConsumer.commitSync();
    }

    private void closeResources() {
        try {
            snapshotProducer.flush();
            log.info("Ресурсы успешно закрыты");
        } catch (Exception e) {
            log.error("Ошибка закрытия продюсера", e);
        }
    }

    public void shutdown() {
        log.info("Инициирован shutdown агрегатора");
        running.set(false);
        if (workerThread != null) {
            workerThread.interrupt();
        }
    }

    @PreDestroy
    public void destroy() {
        shutdown();
    }
}
