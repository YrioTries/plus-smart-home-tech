package ru.yandex.practicum.rest.dto.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.ActionType;

@Getter
@Setter
@ToString
public class DeviceActionDto {
    private String sensorId;
    private ActionType type;
    private Integer value;
}
