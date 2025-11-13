package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.hub.HubEventDto;
import ru.yandex.practicum.dto.sensor.SensorEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.kafka.config.TopicType;
import ru.yandex.practicum.mapper.HubEventMapper;
import ru.yandex.practicum.mapper.SensorEventMapper;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEvent;
import ru.yandex.practicum.kafka.telemetry.event.SensorEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorEventServiceIml implements SensorEventService {
    private final KafkaEventProducer kafkaEventProducer;
    private final SensorEventMapper sensorEventMapper;
    private final HubEventMapper hubEventMapper;

    public void processSensorEvent(SensorEventDto sensorEventDto) {
        SensorEvent sensorEvent = sensorEventMapper.toAvro(sensorEventDto);
        kafkaEventProducer.send(sensorEvent, sensorEventDto.getHubId(), sensorEventDto.getTimestamp(), TopicType.TELEMETRY_SENSORS_V1);
    }

    public void processHubEvent(HubEventDto hubEventDto) {
        HubEvent hubEvent = hubEventMapper.toAvro(hubEventDto);
        kafkaEventProducer.send(hubEvent, hubEventDto.getHubId(), hubEventDto.getTimestamp(), TopicType.TELEMETRY_SENSORS_V1);
    }
}
