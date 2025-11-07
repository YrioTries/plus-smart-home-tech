package dto.sensor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.ConditionType;

@Getter
@Setter
@ToString(callSuper = true)
public class TemperatureSensorEventDto extends SensorEventDto {
    private Integer temperatureC;
    private Integer temperatureF;

    @Override
    public ConditionType getType() {
        return ConditionType.TEMPERATURE_SENSOR_EVENT;
    }
}
