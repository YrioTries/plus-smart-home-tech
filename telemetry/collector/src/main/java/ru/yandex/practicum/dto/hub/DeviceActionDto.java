package ru.yandex.practicum.dto.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;

@Getter
@Setter
@ToString
public class DeviceActionDto {
    private String sensorId;
    private ActionTypeAvro type;
    private Integer value;
}
