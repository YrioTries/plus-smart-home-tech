package ru.yandex.practicum.dto.sensor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.SensorType;

@Getter
@Setter
@ToString(callSuper = true)
public class ClimateSensorEventDto extends SensorEventDto {
    private Integer temperatureC;
    private Integer humidity;
    private Integer co2Level;

    @Override
    public SensorType getType() {
        return SensorType.CLIMATE_SENSOR_EVENT;
    }
}
