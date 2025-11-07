package dto.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioRemovedEventDto extends HubEventDto {
    @NotBlank
    private String name;

    @Override
    public String getType() {
        return "SCENARIO_REMOVED";
    }
}
