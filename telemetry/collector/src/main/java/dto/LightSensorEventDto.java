package dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.ConditionType;

@Getter
@Setter
@ToString(callSuper = true)
public class LightSensorEventDto extends SensorEventDto {
    private Integer linkQuality;
    private Integer luminosity;

    @Override
    public ConditionType getType() {
        return ConditionType.LIGHT_SENSOR_EVENT;
    }
}
