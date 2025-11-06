package service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvroEventProducerService {

    private final KafkaTemplate<String, Object> avroKafkaTemplate;

    public void sendClimateEvent(ClimateSensorEvent event) {
        String topic = "sensor-climate-events";
        avroKafkaTemplate.send(topic, event.getId(), event)
                .thenAccept(result -> log.info("Climate event sent: {}", event.getId()))
                .exceptionally(failure -> {
                    log.error("Failed to send climate event: {}", event.getId(), failure);
                    return null;
                });
    }

    public void sendLightEvent(LightSensorEvent event) {
        String topic = "sensor-light-events";
        avroKafkaTemplate.send(topic, event.getId(), event)
                .thenAccept(result -> log.info("Light event sent: {}", event.getId()))
                .exceptionally(failure -> {
                    log.error("Failed to send light event: {}", event.getId(), failure);
                    return null;
                });
    }

    public void sendMotionEvent(MotionSensorEvent event) {
        String topic = "sensor-motion-events";
        avroKafkaTemplate.send(topic, event.getId(), event)
                .thenAccept(result -> log.info("Motion event sent: {}", event.getId()))
                .exceptionally(failure -> {
                    log.error("Failed to send motion event: {}", event.getId(), failure);
                    return null;
                });
    }

    public void sendSwitchEvent(SwitchSensorEvent event) {
        String topic = "sensor-switch-events";
        avroKafkaTemplate.send(topic, event.getId(), event)
                .thenAccept(result -> log.info("Switch event sent: {}", event.getId()))
                .exceptionally(failure -> {
                    log.error("Failed to send switch event: {}", event.getId(), failure);
                    return null;
                });
    }

    public void sendTemperatureEvent(TemperatureSensorEvent event) {
        String topic = "sensor-temperature-events";
        avroKafkaTemplate.send(topic, event.getId(), event)
                .thenAccept(result -> log.info("Temperature event sent: {}", event.getId()))
                .exceptionally(failure -> {
                    log.error("Failed to send temperature event: {}", event.getId(), failure);
                    return null;
                });
    }
}