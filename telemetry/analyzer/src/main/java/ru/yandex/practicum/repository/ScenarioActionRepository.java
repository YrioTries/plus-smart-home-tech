package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.ScenarioAction;

import java.util.List;

public interface ScenarioActionRepository extends JpaRepository<ScenarioAction, Long> {

    List<ScenarioAction> findAllByScenarioIdIn(List<Long> scenarioIds);
}
