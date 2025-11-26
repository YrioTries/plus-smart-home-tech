package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.config.KafkaConfig;
import ru.yandex.practicum.kafka.config.TopicType;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final AggregationService aggregationService;
    private final KafkaConfig config;


    public void start() {
        Producer<String, SensorsSnapshotAvro> producer = new KafkaProducer<>(config.getProducerConfig());
        Consumer<String, SensorEventAvro> consumer = new KafkaConsumer<>(config.getConsumerConfig());

        try {
            consumer.subscribe(Collections.singletonList("telemetry.sensors.v1"));
            log.info("Подписались на топик telemetry.sensors.v1");

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    log.debug("Получено событие: key={}, offset={}", record.key(), record.offset());

                    try {
                        SensorEventAvro event = record.value();
                        Optional<SensorsSnapshotAvro> updatedSnapshot = aggregationService.updateState(event);

                        if (updatedSnapshot.isPresent()) {
                            SensorsSnapshotAvro snapshot = updatedSnapshot.get();
                            ProducerRecord<String, SensorsSnapshotAvro> producerRecord =
                                    new ProducerRecord<>(TopicType.TELEMETRY_SNAPSHOTS_V1.getTopic(), snapshot.getHubId(), snapshot);

                            producer.send(producerRecord, (metadata, exception) -> {
                                if (exception != null) {
                                    log.error("Ошибка при отправке снапшота: {}", exception.getMessage());
                                } else {
                                    log.info("Снапшот отправлен: hubId={}, offset={}",
                                            snapshot.getHubId(), metadata.offset());
                                }
                            });
                        }
                    } catch (Exception e) {
                        log.error("Ошибка при обработке события: {}", e.getMessage(), e);
                    }
                }
            }

        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {

                producer.flush();

                consumer.commitSync();

            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }
}
