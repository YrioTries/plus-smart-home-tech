package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.entity.Sensor;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorRepository sensorRepository;

    public Optional<Sensor> getSensor(String sensorId, String hubId) {
        return sensorRepository.findByIdAndHubId(sensorId, hubId);
    }

    public boolean sensorExists(String sensorId, String hubId) {
        return sensorRepository.findByIdAndHubId(sensorId, hubId).isPresent();
    }
}
