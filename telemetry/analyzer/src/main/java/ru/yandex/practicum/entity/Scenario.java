package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "scenarios",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"hub_id", "name"})
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hub_id", nullable = false)
    private String hubId;

    @Column(nullable = false)
    private String name;




    // ✅ ВИРТУАЛЬНЫЕ MAP (только для метода!)
    private transient Map<String, Condition> conditions = new HashMap<>();
    private transient Map<String, Action> actions = new HashMap<>();

    // ✅ МЕТОД ДОБАВЛЕНИЯ УСЛОВИЯ (НЕ сохраняет в БД!)
    public void addCondition(String sensorId, Condition condition) {
        conditions.put(sensorId, condition);
    }

    // ✅ МЕТОД ДОБАВЛЕНИЯ ДЕЙСТВИЯ (НЕ сохраняет в БД!)
    public void addAction(String sensorId, Action action) {
        actions.put(sensorId, action);
    }

    // ✅ ГЕТТЕРЫ (ленивая инициализация)
    public Map<String, Condition> getConditions() {
        if (conditions == null) conditions = new HashMap<>();
        return conditions;
    }

    public Map<String, Action> getActions() {
        if (actions == null) actions = new HashMap<>();
        return actions;
    }

    // ✅ ОЧИСТКА
    public void clearConditions() {
        if (conditions != null) conditions.clear();
    }

    public void clearActions() {
        if (actions != null) actions.clear();
    }
}