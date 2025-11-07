package service;

import dto.hub.HubEventDto;
import dto.sensor.SensorEventDto;

import java.util.List;

public interface SensorEventService {
    void processSensorEvent(SensorEventDto event);

    void processHubEvent(HubEventDto event);
}
