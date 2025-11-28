package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.Sensor;

import java.util.Optional;
import java.util.Set;

public interface SensorRepository extends JpaRepository<Sensor, Long> {

    Optional<Sensor> findByIdAndHubId(String id, String hubId);

    boolean existsByIdInAndHubId(Set<String> ids, String hubId);
}
