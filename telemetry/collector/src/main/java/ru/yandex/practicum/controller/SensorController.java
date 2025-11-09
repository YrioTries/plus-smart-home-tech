package ru.yandex.practicum.controller;

import ru.yandex.practicum.dto.hub.HubEventDto;
import ru.yandex.practicum.dto.sensor.SensorEventDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.SensorEventService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class SensorController {
    private final SensorEventService sensorEventService;

    @PostMapping("/sensors")
    @ResponseStatus(HttpStatus.OK)
    public String collectSensorEvent(@Valid @RequestBody SensorEventDto event) {
        sensorEventService.processSensorEvent(event);
        return "Event processed successfully";
    }

    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.OK)
    public String collectHubEvent(@Valid @RequestBody HubEventDto event) {
        sensorEventService.processHubEvent(event);
        return "Event processed successfully";
    }
}
