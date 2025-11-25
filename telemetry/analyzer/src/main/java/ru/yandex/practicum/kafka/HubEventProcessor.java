package ru.yandex.practicum.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.kafka.core.ConsumerFactory;
import ru.yandex.practicum.kafka.telemetry.event.HubEvent;
import ru.yandex.practicum.service.HubEventService;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final HubEventService service;
    private final ConsumerFactory<String, HubEvent> hubEventConsumerFactory;

    @Value("${spring.kafka.topics.hub-topic-name}")
    private String hubEventTopic;

    @Override
    public void run() {
        try(Consumer<String, HubEvent> hubEventConsumer = hubEventConsumerFactory.createConsumer()) {
            hubEventConsumer.subscribe(List.of(hubEventTopic));

            while (true) {
                var hubEvents = hubEventConsumer.poll(Duration.ofMillis(100));
                log.debug("Получено {} записей", hubEvents.count());
                service.saveHubEvent(hubEvents);

                hubEventConsumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка при обработке событий Kafka", e);
        }
    }
}
