package ru.yandex.practicum.kafka.producer;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.config.KafkaSnapshotProducerConfig;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProducer {

    private final KafkaSnapshotProducerConfig producerConfig;

    @Value("${spring.kafka.topics.snapshots-topic-name}")
    private String snapshotsTopic;

    private Producer<String, SpecificRecordBase> producer;

    @PostConstruct
    public void init() {
        producer = producerConfig.createSnapshotProducer();
    }

    public void sendSnapshot(SensorsSnapshotAvro snapshot) {
        try {
            ProducerRecord<String, SpecificRecordBase> record =
                    new ProducerRecord<>(snapshotsTopic, snapshot.getHubId(), snapshot);

            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Ошибка при отправке снапшота для hubId={}", snapshot.getHubId(), exception);
                } else {
                    log.debug("Снапшот успешно отправлен для hubId={}, partition={}, offset={}",
                            snapshot.getHubId(), metadata.partition(), metadata.offset());
                }
            });
        } catch (Exception e) {
            log.error("Ошибка при отправке снапшота в Kafka: hubId={}", snapshot.getHubId(), e);
        }
    }

    public void flush() {
        if (producer != null) {
            producer.flush();
        }
    }

    @PreDestroy
    public void close() {
        if (producer != null) {
            producer.close();
        }
    }
}
