package ru.yandex.practicum.service;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionExecutionService {

    private final ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public void executeActions(String hubId, String scenarioName, List<ru.yandex.practicum.grpc.telemetry.messages.DeviceActionProto> actions) {
        for (ru.yandex.practicum.grpc.telemetry.messages.DeviceActionProto action : actions) {
            try {
                var request = ru.yandex.practicum.grpc.telemetry.hubrouter.DeviceActionRequest.newBuilder()
                        .setHubId(hubId)
                        .setScenarioName(scenarioName)
                        .setAction(action)
                        .build();

                Empty response = hubRouterClient.handleDeviceAction(request);
                log.info("Successfully executed action for device: {} in scenario: {} for hub: {}",
                        action.getSensorId(), scenarioName, hubId);

            } catch (StatusRuntimeException e) {
                if (e.getStatus().getCode() == Status.Code.UNAVAILABLE) {
                    log.error("Hub Router is unavailable for hub: {}", hubId);
                } else {
                    log.error("Failed to execute action for device: {} in scenario: {} for hub: {}. Error: {}",
                            action.getSensorId(), scenarioName, hubId, e.getStatus().getDescription());
                }
            } catch (Exception e) {
                log.error("Unexpected error executing action for device: {} in scenario: {} for hub: {}",
                        action.getSensorId(), scenarioName, hubId, e);
            }
        }
    }
}
