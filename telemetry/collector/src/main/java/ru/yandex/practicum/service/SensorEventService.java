package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.hub.HubEventDto;
import ru.yandex.practicum.dto.sensor.SensorEventDto;
import ru.yandex.practicum.kafka.telemetry.event.HubEvent;
import ru.yandex.practicum.kafka.telemetry.event.SensorEvent;

public interface SensorEventService {
    void processSensorEvent(SensorEvent event);

    void processHubEvent(HubEvent event);
}
