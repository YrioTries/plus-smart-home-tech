package ru.yandex.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.dto.hub.HubEventDto;
import ru.yandex.practicum.dto.sensor.SensorEventDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.SensorEventService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class SensorController {
    private final SensorEventService sensorEventService;

    @PostMapping("/sensors")
    @ResponseStatus(HttpStatus.CREATED)
    public void collectSensorEvent(@Valid @RequestBody SensorEventDto event) {
        log.info("Received sensor event: {}", event);
        sensorEventService.processSensorEvent(event);
    }

    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.CREATED)
    public void collectHubEvent(@Valid @RequestBody HubEventDto event) {
        log.info("Received hub event: {}", event);
        sensorEventService.processHubEvent(event);
    }
}
