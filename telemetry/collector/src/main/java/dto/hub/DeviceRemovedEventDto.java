package dto.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceRemovedEventDto extends HubEventDto {
    @NotBlank
    private String id;

    @Override
    public String getType() {
        return "DEVICE_REMOVED";
    }
}
