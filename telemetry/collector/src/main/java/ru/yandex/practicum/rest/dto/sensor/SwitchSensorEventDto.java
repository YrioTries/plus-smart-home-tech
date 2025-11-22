package ru.yandex.practicum.rest.dto.sensor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.SensorType;

@Getter
@Setter
@ToString(callSuper = true)
public class SwitchSensorEventDto extends SensorEventDto {
    private Boolean state;

    @Override
    public SensorType getType() {
        return SensorType.SWITCH_SENSOR_EVENT;
    }
}
