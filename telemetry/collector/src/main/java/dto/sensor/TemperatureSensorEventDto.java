package dto.sensor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.SensorType;

@Getter
@Setter
@ToString(callSuper = true)
public class TemperatureSensorEventDto extends SensorEventDto {
    private Integer temperatureC;
    private Integer temperatureF;

    @Override
    public SensorType getType() {
        return SensorType.TEMPERATURE_SENSOR_EVENT;
    }
}
