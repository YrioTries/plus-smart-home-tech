package ru.yandex.practicum.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ScenarioConditionId implements Serializable {

    private Long scenarioId;
    private String sensorId;
    private Long conditionId;
}