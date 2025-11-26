package ru.yandex.practicum.dto.sensor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.SensorTypeAvro;

@Getter
@Setter
@ToString(callSuper = true)
public class ClimateSensorEventDto extends SensorEventDto {
    private Integer temperatureC;
    private Integer humidity;
    private Integer co2Level;

    @Override
    public SensorTypeAvro getType() {
        return SensorTypeAvro.CLIMATE_SENSOR_EVENT;
    }
}
