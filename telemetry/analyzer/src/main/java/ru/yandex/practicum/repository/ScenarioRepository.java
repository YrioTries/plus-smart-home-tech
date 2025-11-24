package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.entity.Scenario;

import java.util.List;
import java.util.Optional;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
    List<Scenario> findByHubId(String hubId);
    Optional<Scenario> findByHubIdAndName(String hubId, String name);

    @Query("SELECT s FROM Scenario s JOIN FETCH s.conditions sc JOIN FETCH sc.sensor JOIN FETCH sc.condition " +
            "JOIN FETCH s.actions sa JOIN FETCH sa.sensor JOIN FETCH sa.action " +
            "WHERE s.hubId = :hubId")
    List<Scenario> findByHubIdWithDetails(@Param("hubId") String hubId);

    boolean existsByHubIdAndName(String hubId, String name);
}
