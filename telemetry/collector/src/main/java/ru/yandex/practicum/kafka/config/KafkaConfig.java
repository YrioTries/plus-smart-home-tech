package ru.yandex.practicum.kafka.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.serializer.GeneralAvroDeserializer;
import ru.yandex.practicum.kafka.serializer.GeneralAvroSerializer;

import java.util.Properties;

@Getter
@Component
public class KafkaConfig {
    private Properties producerConfig;
    private Properties consumerConfig;

    @PostConstruct
    public void init() {
        //Producer Config
        this.producerConfig = new Properties();
        this.producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        this.producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.VoidSerializer");
        this.producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class.getName());

        //Consumer Config
        this.consumerConfig = new Properties();
        this.consumerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        this.consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GeneralAvroDeserializer.class.getName());
        this.consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "collector-group");
        this.consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        this.producerConfig.put(ProducerConfig.ACKS_CONFIG, "1"); // Подтверждение от лидера
        this.producerConfig.put(ProducerConfig.LINGER_MS_CONFIG, 5); // Задержка для батчинга
        this.producerConfig.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); // Размер батча
        this.producerConfig.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432); // Память для буфера
    }
}