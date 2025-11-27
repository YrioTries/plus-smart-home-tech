package ru.yandex.practicum.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import ru.practicum.avro.deserializer.HubEventDeserializer;
import ru.practicum.avro.deserializer.SensorsSnapshotDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Configuration
public class AnalyzerConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private final SensorsSnapshotDeserializer snapshotDeserializer;
    private final HubEventDeserializer hubEventDeserializer;

    public AnalyzerConsumerConfig(SensorsSnapshotDeserializer snapshotDeserializer,
                                  HubEventDeserializer hubEventDeserializer) {
        this.snapshotDeserializer = snapshotDeserializer;
        this.hubEventDeserializer = hubEventDeserializer;
    }

    public KafkaConsumer<String, SensorsSnapshotAvro> createSensorsSnapshotConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "analyzer-snapshots-group");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Важно: earliest

        return new KafkaConsumer<>(props, new StringDeserializer(), snapshotDeserializer);
    }

    public KafkaConsumer<String, HubEventAvro> createHubEventConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "analyzer-hub-events-group");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Важно: earliest

        return new KafkaConsumer<>(props, new StringDeserializer(), hubEventDeserializer);
    }
}