package controller;

import dto.sensor.SensorEventDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import service.SensorEventService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class SensorController {

    private final SensorEventService sensorEventService;

    @PostMapping("/sensors")
    @ResponseStatus(HttpStatus.OK)
    public String collectSensorEvent(@Valid @RequestBody SensorEventDto event) {
        sensorEventService.processSensorEvent(event);
        return "Event processed successfully";  // Будет в теле ответа
    }
}
