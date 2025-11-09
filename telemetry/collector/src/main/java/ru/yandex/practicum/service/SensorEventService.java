package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.hub.HubEventDto;
import ru.yandex.practicum.dto.sensor.SensorEventDto;

public interface SensorEventService {
    void processSensorEvent(SensorEventDto event);

    void processHubEvent(HubEventDto event);
}
