package ru.yandex.practicum.dto.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.HubEventTypeAvro;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceRemovedEventDto extends HubEventDto {
    @NotBlank
    private String id;

    @Override
    public HubEventTypeAvro getType() {
        return HubEventTypeAvro.DEVICE_REMOVED;
    }
}
