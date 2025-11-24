package ru.yandex.practicum.dto.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.HubEventType;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioAddedEventDto extends HubEventDto {
    @NotBlank
    @Size(min = 3, max = 2147483647)
    private String name;

    private List<ScenarioConditionDto> conditions;

    private List<DeviceActionDto> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
