package ru.yandex.practicum.dto.sensor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.SensorTypeAvro;

@Getter
@Setter
@ToString(callSuper = true)
public class SwitchSensorEventDto extends SensorEventDto {
    private Boolean state;

    @Override
    public SensorTypeAvro getType() {
        return SensorTypeAvro.SWITCH_SENSOR_EVENT;
    }
}
