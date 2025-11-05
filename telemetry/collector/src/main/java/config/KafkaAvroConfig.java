package config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaAvroConfig { //Комментарии пишет не нейронка, а я, чтобы запомнить и разобраться


    // Аннотация для внедрения значения из application.yml
    // Берет значение по ключу spring.kafka.bootstrap-servers
    // Если ключ не найден, использует значение по умолчанию "localhost:9092"
    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers; // Адреса Kafka брокеров в формате "хост:порт,хост:порт" Например: "kafka1:9092,kafka2:9092"

    // Внедряет URL Schema Registry из конфигурации
    // Schema Registry - отдельный сервис для хранения Avro схем
    @Value("${spring.kafka.schema-registry.url:http://localhost:8081}")
    private String schemaRegistryUrl; // URL где запущен Confluent Schema Registry

    @Bean
    public ProducerFactory<String, Object> avroProducerFactory() {
        // Фабрика для создания Kafka Producer'ов
        // <String, Object> - ключ будет String, значение - любой Object (наши Avro классы)

        Map<String, Object> props = new HashMap<>();
        // Создаем Map для хранения конфигурационных свойств Kafka Producer

        // Базовые настройки Kafka
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // Указывает адреса Kafka брокеров для подключения
        // Producer будет подключаться к этим адресам

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringSerializer.class);
        // Класс для сериализации КЛЮЧЕЙ сообщений
        // StringSerializer преобразует Java String в байты для Kafka

        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        // Класс для сериализации ЗНАЧЕНИЙ сообщений
        // KafkaAvroSerializer преобразует Avro объекты в байты используя Schema Registry

        // Настройки Schema Registry для Avro
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        // URL Schema Registry, где хранятся Avro схемы
        // Сериализатор будет регистрировать и загружать схемы отсюда

        props.put(AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS, true);
        // Автоматически регистрировать новые схемы в Schema Registry
        // При отправке нового типа сообщения схема будет зарегистрирована автоматически

        // Настройки надежности доставки
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        // Требует подтверждения от ВСЕХ реплик брокера
        // "all" - самая надежная настройка, но медленнее
        // "1" - подтверждение только от лидера (быстрее)
        // "0" - без подтверждения (самое быстрое, но ненадежно)

        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        // Количество попыток повторной отправки при ошибках
        // 3 означает, что Producer попробует отправить сообщение 3 раза прежде чем сдаться

        return new DefaultKafkaProducerFactory<>(props);
        // Создает и возвращает фабрику Producer'ов с нашими настройками
        // Spring будет использовать эту фабрику для создания KafkaProducer'ов
    }

    @Bean
    public KafkaTemplate<String, Object> avroKafkaTemplate() {
        // Создает бин KafkaTemplate - основной класс для отправки сообщений в Spring Kafka
        // KafkaTemplate это обертка над KafkaProducer с дополнительными возможностями Spring

        return new KafkaTemplate<>(avroProducerFactory());
        // Создает KafkaTemplate используя нашу фабрику
        // KafkaTemplate предоставляет удобные методы для отправки сообщений
        // и интегрируется с Spring features (транзакции, ошибки и т.д.)
    }
}
