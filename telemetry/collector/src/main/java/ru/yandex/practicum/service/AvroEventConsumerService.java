package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEvent;
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
                ru.yandex.practicum.kafka.telemetry.event.ClimateSensorEvent payloadClimate =
                        (ru.yandex.practicum.kafka.telemetry.event.ClimateSensorEvent) event.getPayload();
                log.info("Received Climate Event - ID: {}, Temp: {}, Humidity: {}",
                        event.getId(), payloadClimate.getTemperatureC(), payloadClimate.getHumidity());
                break;
            case LIGHT_SENSOR_EVENT:
                ru.yandex.practicum.kafka.telemetry.event.LightSensorEvent payloadLight =
                        (ru.yandex.practicum.kafka.telemetry.event.LightSensorEvent) event.getPayload();
                log.info("Received Light Event - ID: {}, Luminosity: {}",
                        event.getId(), payloadLight.getLuminosity());
                break;
            case MOTION_SENSOR_EVENT:
                ru.yandex.practicum.kafka.telemetry.event.MotionSensorEvent payloadMotion =
                        (ru.yandex.practicum.kafka.telemetry.event.MotionSensorEvent) event.getPayload();
                log.info("Received Motion Event - ID: {}, Motion: {}",
                        event.getId(), payloadMotion.getMotion());
                break;
            case SWITCH_SENSOR_EVENT:
                ru.yandex.practicum.kafka.telemetry.event.SwitchSensorEvent payloadSwitch =
                        (ru.yandex.practicum.kafka.telemetry.event.SwitchSensorEvent) event.getPayload();
                log.info("Received Switch Event - ID: {}, State: {}",
                        event.getId(), payloadSwitch.getState());
                break;
            case TEMPERATURE_SENSOR_EVENT:
                ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorEvent payloadTemperature =
                        (ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorEvent) event.getPayload();
                log.info("Received Temperature Event - ID: {}, TempC: {}, TempF: {}",
                        event.getId(), payloadTemperature.getTemperatureC(), payloadTemperature.getTemperatureF());
                break;
            default:
                log.warn("Unknown sensor event type: {}", event.getType());
        }
    }

    @KafkaListener(topics = "telemetry.hubs.v1")
    public void consumeHubEvent(HubEvent event) {
        switch (event.getType()) {
            case DEVICE_ADDED:
                ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEvent payloadDeviceAdded =
                        (ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEvent) event.getPayload();
                log.info("Received Device Added Event - Hub ID: {}, Device ID: {}, Device Type: {}",
                        event.getHubId(), payloadDeviceAdded.getId(), payloadDeviceAdded.getDeviceType());
                break;
            case DEVICE_REMOVED:
                ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEvent payloadDeviceRemoved =
                        (ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEvent) event.getPayload();
                log.info("Received Device Removed Event - Hub ID: {}, Device ID: {}",
                        event.getHubId(), payloadDeviceRemoved.getId());
                break;
            case SCENARIO_ADDED:
                ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEvent payloadScenarioAdded =
                        (ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEvent) event.getPayload();
                log.info("Received Scenario Added Event - Hub ID: {}, Scenario Name: {}",
                        event.getHubId(), payloadScenarioAdded.getName());
                break;
            case SCENARIO_REMOVED:
                ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEvent payloadScenarioRemoved =
                        (ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEvent) event.getPayload();
                log.info("Received Scenario Removed Event - Hub ID: {}, Scenario Name: {}",
                        event.getHubId(), payloadScenarioRemoved.getName());
                break;
            default:
                log.warn("Unknown hub event type: {}", event.getType());
        }
    }
}
