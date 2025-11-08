package dto.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.HubEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioRemovedEventDto extends HubEventDto {
    @NotBlank
    private String name;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
