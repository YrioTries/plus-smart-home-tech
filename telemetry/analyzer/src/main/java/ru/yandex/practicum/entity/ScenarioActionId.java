package ru.yandex.practicum.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ScenarioActionId implements Serializable {

    private Long scenarioId;
    private String sensorId;
    private Long actionId;
}
