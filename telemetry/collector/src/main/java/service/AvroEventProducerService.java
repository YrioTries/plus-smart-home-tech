package service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AvroEventProducerService {
    // Внедряем KafkaTemplate из KafkaAvroConfig
    private final KafkaTemplate<String, Object> avroKafkaTemplate;

    // Метод для отправки ClimateSensorEvent
    public void sendClimateEvent(ClimateSensorEvent event) {
        String topic = "sensor-climate-events";
        avroKafkaTemplate.send(topic, event.getId(), event)
                .addCallback(
                        result -> log.info("✅ Climate event sent: {}", event.getId()),
                        failure -> log.error("❌ Failed to send climate event: {}", event.getId())
                );
    }

    // Метод для отправки LightSensorEvent
    public void sendLightEvent(LightSensorEvent event) {
        String topic = "sensor-light-events";
        avroKafkaTemplate.send(topic, event.getId(), event)
                .addCallback(
                        result -> log.info("✅ Light event sent: {}", event.getId()),
                        failure -> log.error("❌ Failed to send light event: {}", event.getId())
                );
    }

    // Добавьте аналогичные методы для других типов событий:
    // sendMotionEvent, sendSwitchEvent, sendTemperatureEvent
}
