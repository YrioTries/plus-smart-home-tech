package dto.sensor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.ConditionType;

@Getter
@Setter
@ToString(callSuper = true)
public class ClimateSensorEventDto extends SensorEventDto {
    private Integer temperatureC;
    private Integer humidity;
    private Integer co2Level;

    @Override
    public ConditionType getType() {
        return ConditionType.CLIMATE_SENSOR_EVENT;
    }
}
