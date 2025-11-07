package service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvroEventProducerService {
    private final KafkaTemplate<String, Object> avroKafkaTemplate;

    public void sendSensorEvent(SensorEvent event) {
        String topic = "telemetry.sensors";
        avroKafkaTemplate.send(topic, event.getId(), event)
                .thenAccept(result -> log.info("Event sent: {}", event.getId()))
                .exceptionally(failure -> {
                    log.error("Failed to send event: {}", event.getId(), failure);
                    return null;
                });
    }
}
