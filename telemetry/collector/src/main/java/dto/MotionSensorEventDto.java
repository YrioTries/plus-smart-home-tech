package dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.ConditionType;

@Getter
@Setter
@ToString(callSuper = true)
public class MotionSensorEventDto extends SensorEventDto {
    private Integer linkQuality;
    private Boolean motion;
    private Integer voltage;

    @Override
    public ConditionType getType() {
        return ConditionType.MOTION_SENSOR_EVENT;
    }
}
