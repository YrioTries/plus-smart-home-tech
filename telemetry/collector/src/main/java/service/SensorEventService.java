package service;

import dto.sensor.SensorEventDto;

import java.util.List;

public interface SensorEventService {
    void processSensorEvent(SensorEventDto event);
}
