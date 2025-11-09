package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.hub.HubEventDto;
import ru.yandex.practicum.dto.sensor.SensorEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.mapper.HubEventMapper;
import ru.yandex.practicum.mapper.SensorEventMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.HubEvent;
import ru.yandex.practicum.kafka.telemetry.event.SensorEvent;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SensorEventServiceIml implements SensorEventService {
    private final AvroEventProducerService avroEventProducerService;
    private final SensorEventMapper sensorEventMapper;
    private final HubEventMapper hubEventMapper;

    @Transactional
    public void processSensorEvent(SensorEventDto eventDto) {
        SensorEvent sensorEvent = sensorEventMapper.toAvro(eventDto);
        avroEventProducerService.sendSensorEvent(sensorEvent);
    }

    @Transactional
    public void processHubEvent(HubEventDto eventDto) {
        HubEvent hubEvent = hubEventMapper.toAvro(eventDto);
        avroEventProducerService.sendHubEvent(hubEvent);
    }
}
