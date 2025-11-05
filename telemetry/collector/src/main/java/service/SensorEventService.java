package service;

import dto.SensorEventDto;

import java.util.List;

public interface SensorEventService {
    String processSensorEvent(SensorEventDto event);

    void processSensorEvents(String hubId, List<SensorEventDto> events);
}
