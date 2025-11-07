package dto.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.DeviceType;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAddedEventDto extends HubEventDto {
    @NotBlank
    private String id;

    @NotBlank
    private DeviceType deviceType;

    @Override
    public String getType() {
        return "DEVICE_ADDED";
    }
}
