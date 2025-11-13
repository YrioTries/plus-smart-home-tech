package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.kafka.config.TopicType;
import ru.yandex.practicum.mapper.HubEventMapper;
import ru.yandex.practicum.mapper.SensorEventMapper;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEvent;
import ru.yandex.practicum.kafka.telemetry.event.SensorEvent;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorEventServiceIml implements SensorEventService {
    private final KafkaEventProducer kafkaEventProducer;
    private final SensorEventMapper sensorEventMapper;
    private final HubEventMapper hubEventMapper;

    public void processSensorEvent(SensorEvent sensorEvent) {
        //SensorEvent sensorEvent = sensorEventMapper.toAvro(sensorEvent);
        kafkaEventProducer.send(sensorEvent, sensorEvent.getHubId(), Instant.ofEpochMilli(sensorEvent.getTimestamp()), TopicType.TELEMETRY_SENSORS_V1);
    }

    public void processHubEvent(HubEvent hubEvent) {
        //HubEvent hubEvent = hubEventMapper.toAvro(hubEventDto);
        kafkaEventProducer.send(hubEvent, hubEvent.getHubId(), Instant.ofEpochMilli(hubEvent.getTimestamp()), TopicType.TELEMETRY_HUBS_V1);
    }
}
