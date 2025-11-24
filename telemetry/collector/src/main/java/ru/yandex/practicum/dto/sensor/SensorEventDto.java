package ru.yandex.practicum.dto.sensor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.SensorType;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)

@JsonSubTypes({
        @JsonSubTypes.Type(value = LightSensorEventDto.class, name = "LIGHT_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = ClimateSensorEventDto.class, name = "CLIMATE_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = MotionSensorEventDto.class, name = "MOTION_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = SwitchSensorEventDto.class, name = "SWITCH_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = TemperatureSensorEventDto.class, name = "TEMPERATURE_SENSOR_EVENT")
})

@Getter
@Setter
@ToString
public abstract class SensorEventDto {
    @NotBlank
    private String id;

    @NotBlank
    private String hubId;

    private Instant timestamp = Instant.now();

    @JsonProperty("type")
    public abstract SensorType getType();
}
