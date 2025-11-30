package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "scenario_actions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ScenarioAction {

    @EmbeddedId
    private ScenarioActionId id;

    @ManyToOne
    @JoinColumn(name = "scenario_id")
    @MapsId("scenarioId")
    private Scenario scenario;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    @MapsId("sensorId")
    private Sensor sensor;

    @ManyToOne
    @JoinColumn(name = "action_id")
    @MapsId("actionId")
    private Action action;
}
