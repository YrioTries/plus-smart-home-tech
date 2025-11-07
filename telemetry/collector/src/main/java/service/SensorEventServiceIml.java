package service;

import dto.sensor.SensorEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mapper.SensorEventMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.SensorEvent;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SensorEventServiceIml implements SensorEventService {
    private final AvroEventProducerService avroEventProducerService;
    private final SensorEventMapper sensorEventMapper;

    @Transactional
    public void processSensorEvent(SensorEventDto eventDto) {
        SensorEvent sensorEvent = sensorEventMapper.toAvro(eventDto);
        avroEventProducerService.sendSensorEvent(sensorEvent);
    }
}
