package ru.yandex.practicum.dto.sensor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.SensorType;

@Getter
@Setter
@ToString(callSuper = true)
public class LightSensorEventDto extends SensorEventDto {
    private Integer linkQuality;
    private Integer luminosity;

    @Override
    public SensorType getType() {
        return SensorType.LIGHT_SENSOR_EVENT;
    }
}
