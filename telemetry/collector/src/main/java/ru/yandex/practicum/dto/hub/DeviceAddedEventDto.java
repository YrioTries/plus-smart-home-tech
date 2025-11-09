package ru.yandex.practicum.dto.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.DeviceType;
import ru.yandex.practicum.kafka.telemetry.event.HubEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAddedEventDto extends HubEventDto {
    @NotBlank
    private String id;

    @NotBlank
    private DeviceType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}
