package service;

import dto.SensorEventDto;

public interface SensorEventService {
    String processSensorEvent(SensorEventDto event);
}
