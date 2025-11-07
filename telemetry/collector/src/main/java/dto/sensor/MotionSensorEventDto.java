package dto.sensor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.SensorType;

@Getter
@Setter
@ToString(callSuper = true)
public class MotionSensorEventDto extends SensorEventDto {
    private Integer linkQuality;
    private Boolean motion;
    private Integer voltage;

    @Override
    public SensorType getType() {
        return SensorType.MOTION_SENSOR_EVENT;
    }
}
