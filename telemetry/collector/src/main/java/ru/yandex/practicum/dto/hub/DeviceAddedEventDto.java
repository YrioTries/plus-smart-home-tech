package ru.yandex.practicum.dto.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventTypeAvro;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAddedEventDto extends HubEventDto {
    @NotBlank
    private String id;

    private DeviceTypeAvro deviceType;

    @Override
    public HubEventTypeAvro getType() {
        return HubEventTypeAvro.DEVICE_ADDED;
    }
}
