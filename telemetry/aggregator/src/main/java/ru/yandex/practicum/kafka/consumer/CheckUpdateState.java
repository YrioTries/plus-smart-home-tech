package ru.yandex.practicum.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class CheckUpdateState {

    private final Map<String, SensorsSnapshotAvro> snapshotsMap = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        SensorsSnapshotAvro snapshot = snapshotsMap.computeIfAbsent(event.getHubId(), hub ->
                SensorsSnapshotAvro.newBuilder()
                        .setHubId(hub)
                        .setSensorsState(new HashMap<>())
                        .setTimestamp(event.getTimestamp()) // long - правильно
                        .build()
        );

        SensorStateAvro currentState = snapshot.getSensorsState().get(event.getId());
        if (currentState != null &&
                event.getTimestamp() <= currentState.getTimestamp() && // Исправлено сравнение
                event.getPayload().equals(currentState.getData())) {
            return Optional.empty();
        }

        snapshot.getSensorsState().put(event.getId(), buildSensorState(event));
        snapshot.setTimestamp(event.getTimestamp());
        return Optional.of(snapshot);
    }

    public SensorStateAvro buildSensorState(SensorEventAvro event) {
        Object payload = event.getPayload();

        if (!(payload instanceof ClimateSensorEventAvro
                || payload instanceof LightSensorEventAvro
                || payload instanceof MotionSensorEventAvro
                || payload instanceof SwitchSensorEventAvro
                || payload instanceof TemperatureSensorEventAvro)) {
            throw new IllegalArgumentException("Payload имеет неверный тип: " + payload.getClass());
        }

        return SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(payload)
                .build();
    }

    public void putSnapshot(SensorsSnapshotAvro snapshot) {
        snapshotsMap.put(snapshot.getHubId(), snapshot);
    }
}
