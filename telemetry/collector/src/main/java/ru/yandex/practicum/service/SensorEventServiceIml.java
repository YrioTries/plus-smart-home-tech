package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.dto.hub.*;
import ru.yandex.practicum.dto.sensor.*;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.kafka.config.TopicType;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.mapper.HubEventMapper;
import ru.yandex.practicum.mapper.SensorEventMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorEventServiceIml implements SensorEventService {
    private final KafkaEventProducer kafkaEventProducer;
    private final SensorEventMapper sensorEventMapper;
    private final HubEventMapper hubEventMapper;

    public void processSensorEvent(SensorEventDto sensorEventDto) {
        log.info("Processing sensor event: {}", sensorEventDto);
        try {
            SensorEvent sensorEvent = sensorEventMapper.toAvro(sensorEventDto);

            kafkaEventProducer.send(
                    sensorEvent,
                    sensorEventDto.getHubId(),
                    sensorEventDto.getTimestamp(),
                    TopicType.TELEMETRY_SENSORS_V1
            );
        } catch (Exception e) {
            log.error("Error processing sensor event: {}", sensorEventDto, e);
            throw e;
        }
    }

    public void processHubEvent(HubEventDto hubEventDto) {
        log.info("Processing hub event: {}", hubEventDto);
        try {
            HubEvent hubEvent = hubEventMapper.toAvro(hubEventDto);

            kafkaEventProducer.send(
                    hubEvent,
                    hubEventDto.getHubId(),
                    hubEventDto.getTimestamp(),
                    TopicType.TELEMETRY_HUBS_V1
            );
        } catch (Exception e) {
            log.error("Error processing hub event: {}", hubEventDto, e);
            throw e;
        }

    }
}
