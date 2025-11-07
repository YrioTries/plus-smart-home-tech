package dto.sensor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.ConditionType;

@Getter
@Setter
@ToString(callSuper = true)
public class SwitchSensorEventDto extends SensorEventDto {
    private Boolean state;

    @Override
    public ConditionType getType() {
        return ConditionType.SWITCH_SENSOR_EVENT;
    }
}
