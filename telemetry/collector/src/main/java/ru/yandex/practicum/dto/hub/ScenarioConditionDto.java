package ru.yandex.practicum.dto.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

@Getter
@Setter
@ToString
public class ScenarioConditionDto<T> {
    private String sensorId;
    private ConditionTypeAvro type;
    private ConditionOperationAvro operation;
    private T value;
}
