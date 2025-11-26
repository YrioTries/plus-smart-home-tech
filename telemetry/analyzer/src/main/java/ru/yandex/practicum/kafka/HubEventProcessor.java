package ru.yandex.practicum.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.kafka.telemetry.event.HubEvent;
import ru.yandex.practicum.service.HubEventService;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class HubEventProcessor implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(HubEventProcessor.class);
    private final HubEventService service;
    private final String hubEventTopic;
    private final KafkaConsumer<String, HubEvent> hubEventConsumer;

    public HubEventProcessor(HubEventService service, String hubEventTopic) {
        this.service = service;
        this.hubEventTopic = hubEventTopic;

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "hub-event-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GeneralAvroDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        this.hubEventConsumer = new KafkaConsumer<>(props);
        this.hubEventConsumer.subscribe(Collections.singletonList(hubEventTopic));
    }

    @Override
    public void run() {
        try {
            while (true) {
                ConsumerRecords<String, HubEvent> hubEvents = hubEventConsumer.poll(Duration.ofMillis(100));
                log.debug("Получено {} записей", hubEvents.count());
                service.saveHubEvent(hubEvents);
                hubEventConsumer.commitSync();
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке событий Kafka", e);
        } finally {
            hubEventConsumer.close();
        }
    }
}

