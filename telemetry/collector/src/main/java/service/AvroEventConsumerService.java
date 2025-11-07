package service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvroEventConsumerService {

    @KafkaListener(topics = "sensor-climate-events")
    public void consumeClimateEvent(ClimateSensorEvent event) {
        log.info("📥 Received Climate Event - ID: {}, Temp: {}, Humidity: {}",
                event.getId(), event.getTemperatureC(), event.getHumidity());
        // Здесь бизнес-логика обработки климатических событий
    }

    @KafkaListener(topics = "sensor-light-events")
    public void consumeLightEvent(LightSensorEvent event) {
        log.info("📥 Received Light Event - ID: {}, Luminosity: {}",
                event.getId(), event.getLuminosity());
        // Обработка событий освещения
    }

    @KafkaListener(topics = "sensor-motion-events")
    public void consumeMotionEvent(MotionSensorEvent event) {
        log.info("📥 Received Motion Event - ID: {}, Motion: {}",
                event.getId(), event.getMotion());
        // Обработка событий движения
    }

    @KafkaListener(topics = "sensor-switch-events")
    public void consumeSwitchEvent(SwitchSensorEvent event) {
        log.info("📥 Received Switch Event - ID: {}, State: {}",
                event.getId(), event.getState());
        // Обработка событий переключателей
    }

    @KafkaListener(topics = "sensor-temperature-events")
    public void consumeTemperatureEvent(TemperatureSensorEvent event) {
        log.info("📥 Received Temperature Event - ID: {}, TempC: {}, TempF: {}",
                event.getId(), event.getTemperatureC(), event.getTemperatureF());
        // Обработка температурных событий
    }
}