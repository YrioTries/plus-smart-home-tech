package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregationService {

    private final Map<String, SensorsSnapshotAvro> snapshots = new ConcurrentHashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEvent event) {
        String hubId = event.getHubId();
        String deviceId = event.getId();

        SensorsSnapshotAvro snapshot = snapshots.get(hubId);
        if (snapshot == null) {
            snapshot = SensorsSnapshotAvro.newBuilder()
                    .setHubId(hubId)
                    .setTimestamp(event.getTimestamp())
                    .setSensorsState(new ArrayList<>())
                    .build();
        }

        List<DeviceState> sensorsState = new ArrayList<>(snapshot.getSensorsState());
        DeviceState existingDeviceState = null;
        int existingIndex = -1;

        for (int i = 0; i < sensorsState.size(); i++) {
            DeviceState deviceState = sensorsState.get(i);
            if (deviceId.equals(deviceState.getDeviceId())) {
                existingDeviceState = deviceState;
                existingIndex = i;
                break;
            }
        }

        if (existingDeviceState != null) {
            SensorStateAvro oldState = existingDeviceState.getState();

            if (oldState.getTimestamp() > event.getTimestamp()) {
                log.debug("Событие устарело для устройства: {}", deviceId);
                return Optional.empty();
            }

            if (dataEquals(oldState.getData(), event.getPayload())) {
                log.debug("Данные не изменились для устройства: {}", deviceId);
                return Optional.empty();
            }
        }

        SensorStateAvro newSensorState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        DeviceState newDeviceState = DeviceState.newBuilder()
                .setDeviceId(deviceId)
                .setState(newSensorState)
                .build();

        if (existingIndex != -1) {
            sensorsState.set(existingIndex, newDeviceState);
        } else {
            sensorsState.add(newDeviceState);
        }

        SensorsSnapshotAvro updatedSnapshot = SensorsSnapshotAvro.newBuilder()
                .setHubId(hubId)
                .setTimestamp(event.getTimestamp())
                .setSensorsState(sensorsState)
                .build();

        snapshots.put(hubId, updatedSnapshot);

        log.info("Снапшот обновлен для хаба: {}, устройство: {}", hubId, deviceId);
        return Optional.of(updatedSnapshot);
    }

    private boolean dataEquals(Object oldData, Object newData) {
        if (oldData == null && newData == null) return true;
        if (oldData == null || newData == null) return false;
        return oldData.equals(newData);
    }

    public Map<String, SensorsSnapshotAvro> getSnapshots() {
        return new ConcurrentHashMap<>(snapshots);
    }
}
