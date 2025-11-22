package ru.yandex.practicum.rest.service;

import ru.yandex.practicum.rest.dto.hub.HubEventDto;
import ru.yandex.practicum.rest.dto.sensor.SensorEventDto;

public interface SensorEventService {
    void processSensorEvent(SensorEventDto event);

    void processHubEvent(HubEventDto event);
}
