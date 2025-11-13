package ru.yandex.practicum.kafka.config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;

public class KafkaConfig {

    private final String bootstrapServers;
    private final String schemaRegistryUrl;

    public KafkaConfig(String bootstrapServers, String schemaRegistryUrl) {
        this.bootstrapServers = bootstrapServers;
        this.schemaRegistryUrl = schemaRegistryUrl;
    }

    public KafkaConfig() {
        this("localhost:9092", "http://localhost:8080");
    }

    public KafkaProducer<String, SpecificRecordBase> getKafkaProducer() {
        Properties props = new Properties();

        // Basic Kafka configuration
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "io.confluent.kafka.serializers.KafkaAvroSerializer");

        // Schema Registry configuration
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        props.put(KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS, "true");

        // Reliability configuration
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, "3");
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, "60000");
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "30000");

        // Optional: batching and performance settings
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        props.put(ProducerConfig.LINGER_MS_CONFIG, "10");
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");

        return new KafkaProducer<>(props);
    }
}