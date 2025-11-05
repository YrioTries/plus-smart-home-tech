package service;

import dto.SensorEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mapper.SensorEventMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SensorEventServiceIml implements SensorEventService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SensorEventMapper sensorEventMapper;

    @Override
    @Transactional
    public String processSensorEvent(SensorEventDto event) {
        return new String();
    }

    @Override
    @Transactional
    public void processSensorEvents(String hubId, List<SensorEventDto> events) {

    }

    private boolean isHealthy() {
        return true;
    }
}
