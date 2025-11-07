package dto.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperation;
import ru.yandex.practicum.kafka.telemetry.event.ConditionType;

@Getter
@Setter
@ToString
public class ScenarioConditionDto<T> {
    private String sensorId;
    private ConditionType type;
    private ConditionOperation operation;
    private T value;
}
