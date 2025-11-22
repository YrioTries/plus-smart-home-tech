package ru.yandex.practicum.rest.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.rest.kafka.config.KafkaConfig;
import ru.yandex.practicum.rest.kafka.config.TopicType;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEvent;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * Класс для отправки сообщений в кафку
 */
@Slf4j
@Component
public class KafkaEventProducer implements AutoCloseable {

    private final KafkaProducer<String, SpecificRecordBase> producer;

    /**
     * Конструктор класса.
     *
     * @param kafkaConfig Класс содержащий настройки для работы с kafka
     */
    public KafkaEventProducer(KafkaConfig kafkaConfig) {
        log.info("Kafka configuration: {}", kafkaConfig.getProducerConfig());
        // Создаём продюсера используя настройки из конфигурации приложения
        this.producer = new KafkaProducer<>(kafkaConfig.getProducerConfig());
    }

    /**
     * Обрабатывает событие от датчика и сохраняет его в топик Kafka.
     * @param event     Событие от датчика
     * @param hubId     Идентификатор хаба, в котором зарегистрирован датчик
     * @param timestamp Метка времени, когда произошло событие
     * @param topicType Тип топика который нужно использовать для отправки сообщения
     */
    public void send(SpecificRecordBase event, String hubId, Instant timestamp, TopicType topicType) {
        String topic = topicType.getTopic();

        // Логирование диагностической информации
        String eventClass = event.getClass().getSimpleName();
        logDiagnosticInfo(event, hubId, eventClass);

        // Формируем запись для отправки в топик, при этом указываем ключ записи - это id хаба
        // это означает, что запись будет сохраняться в партицию в зависимости от id хаба, а это
        // в свою очередь означает, что записи относящиеся к одному хабу можно будет читать упорядоченно
        // т.к. кафка гарантирует очередность сообщений только в рамках партиции.
        // Также мы указываем таймстемп записи и используем для этого время возникновения события
        // это значит, что кафка будет упорядочивать записи по времени возникновения события, а не времени
        // когда брокер кафки получил сообщение
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic,                    // Имя топика куда будет осуществлена запись
                null,                     // Номер партиции (если null, то используется ключ для вычисления раздела)
                timestamp.toEpochMilli(), // Метка времени события
                hubId,                    // Ключ события
                event                     // Значение события
        );

        // Логирование сохранения события
        log.trace("Сохраняю событие {} связанное с хабом {} в топик {}",
                eventClass, hubId, topic);

        Future<RecordMetadata> futureResult = producer.send(record);
        producer.flush();
        try {
            RecordMetadata metadata = futureResult.get();
            log.info("Событие {} было успешно сохранёно в топик {} в партицию {} со смещением {}",
                    eventClass, metadata.topic(), metadata.partition(), metadata.offset());
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Не удалось записать событие {} в топик {}", eventClass, topic, e);
        }
    }

    /**
     * Метод для закрытия ресурсов, связанных с обработчиком.
     * Завершает отправку сообщений в Kafka и закрывает продюсера.
     */
    @Override
    public void close() {
        // Данный метод из AutoCloseable, Spring будет автоматически добавлять бины
        // которые реализуют AutoCloseable/Disposable в shutdown hook при закрытии jvm
        // и вызывать их метод close

        // отправляем оставшиеся данные и закрываем продюсер
        producer.flush();
        producer.close(Duration.ofSeconds(10));
    }

    private void logDiagnosticInfo(SpecificRecordBase event, String hubId, String eventClass) {
        if (event instanceof HubEvent) {
            HubEvent hubEvent = (HubEvent) event;
            Object payload = hubEvent.getPayload();

            log.info("🔍 KAFKA DIAGNOSTICS - Before send:");
            log.info("🔍   Event class: {}", eventClass);
            log.info("🔍   HubId: {}", hubId);
            log.info("🔍   Payload class: {}", payload != null ? payload.getClass().getSimpleName() : "null");
            log.info("🔍   Payload is null: {}", payload == null);

            if (payload != null) {
                // Безопасное получение схемы
                logPayloadSchema(payload);

                // Детали для конкретных типов
                logPayloadDetails(payload);
            }
        } else {
            log.info("🔍 KAFKA DIAGNOSTICS - Non-HubEvent:");
            log.info("🔍   Event class: {}", eventClass);
            log.info("🔍   HubId: {}", hubId);
            log.info("🔍   Event schema name: {}", event.getSchema().getName());
            log.info("🔍   Event schema full: {}", event.getSchema().getFullName());
        }
    }

    /**
     * Безопасно логирует информацию о схеме payload
     */
    private void logPayloadSchema(Object payload) {
        try {
            if (payload instanceof SpecificRecordBase) {
                SpecificRecordBase specificRecord = (SpecificRecordBase) payload;
                Schema schema = specificRecord.getSchema();
                log.info("🔍   Payload schema name: {}", schema.getName());
                log.info("🔍   Payload schema full: {}", schema.getFullName());
            } else {
                log.info("🔍   Payload is not SpecificRecordBase, cannot get schema");
            }
        } catch (Exception e) {
            log.warn("🔍   Failed to get payload schema: {}", e.getMessage());
        }
    }

    /**
     * Логирует детальную информацию о конкретных типах payload
     */
    private void logPayloadDetails(Object payload) {
        if (payload instanceof DeviceAddedEvent) {
            DeviceAddedEvent deviceEvent = (DeviceAddedEvent) payload;
            log.info("🔍   DeviceAddedEvent - id: {}, type: {}",
                    deviceEvent.getId(), deviceEvent.getDeviceType());
        } else if (payload instanceof ScenarioAddedEvent) {
            ScenarioAddedEvent scenarioEvent = (ScenarioAddedEvent) payload;
            log.info("🔍   ScenarioAddedEvent - name: {}, conditions: {}, actions: {}",
                    scenarioEvent.getName(),
                    scenarioEvent.getConditions().size(),
                    scenarioEvent.getActions().size());
        } else {
            log.info("🔍   Payload type: {}", payload.getClass().getSimpleName());
        }
    }
}