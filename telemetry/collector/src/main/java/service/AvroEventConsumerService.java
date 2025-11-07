package service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.SensorType;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvroEventConsumerService {

    @KafkaListener(topics = "telemetry.sensors")
    public void consumeSensorEvent(SensorEvent event) {
        switch (event.getType()) {
            case CLIMATE_SENSOR_EVENT:
                ru.yandex.practicum.kafka.telemetry.event.ClimateSensor payloadClimate =
                        (ru.yandex.practicum.kafka.telemetry.event.ClimateSensor) event.getPayload();
                log.info("Received Climate Event - ID: {}, Temp: {}, Humidity: {}",
                        event.getId(), payloadClimate.getTemperatureC(), payloadClimate.getHumidity());
                break;
            case LIGHT_SENSOR_EVENT:
                ru.yandex.practicum.kafka.telemetry.event.LightSensor payloadLight =
                        (ru.yandex.practicum.kafka.telemetry.event.LightSensor) event.getPayload();
                log.info("Received Light Event - ID: {}, Luminosity: {}",
                        event.getId(), payloadLight.getLuminosity());
                break;
            case MOTION_SENSOR_EVENT:
                ru.yandex.practicum.kafka.telemetry.event.MotionSensor payloadMotion =
                        (ru.yandex.practicum.kafka.telemetry.event.MotionSensor) event.getPayload();
                log.info("Received Motion Event - ID: {}, Motion: {}",
                        event.getId(), payloadMotion.getMotion());
                break;
            case SWITCH_SENSOR_EVENT:
                ru.yandex.practicum.kafka.telemetry.event.SwitchSensor payloadSwitch =
                        (ru.yandex.practicum.kafka.telemetry.event.SwitchSensor) event.getPayload();
                log.info("Received Switch Event - ID: {}, State: {}",
                        event.getId(), payloadSwitch.getState());
                break;
            case TEMPERATURE_SENSOR_EVENT:
                ru.yandex.practicum.kafka.telemetry.event.TemperatureSensor payloadTemperature =
                        (ru.yandex.practicum.kafka.telemetry.event.TemperatureSensor) event.getPayload();
                log.info("Received Temperature Event - ID: {}, TempC: {}, TempF: {}",
                        event.getId(), payloadTemperature.getTemperatureC(), payloadTemperature.getTemperatureF());
                break;
            default:
                log.warn("Unknown sensor event type: {}", event.getType());
        }
    }
}
