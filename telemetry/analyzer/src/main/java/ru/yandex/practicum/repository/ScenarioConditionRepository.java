package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.ScenarioCondition;

import java.util.List;

public interface ScenarioConditionRepository extends JpaRepository<ScenarioCondition, Long> {

    List<ScenarioCondition> findAllByScenarioIdIn(List<Long> scenarioIds);
}
