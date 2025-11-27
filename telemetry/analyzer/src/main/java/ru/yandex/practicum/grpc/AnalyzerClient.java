package ru.yandex.practicum.grpc;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.messages.DeviceActionRequest;

@Slf4j
@Service
public class AnalyzerClient {

    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public AnalyzerClient(@GrpcClient("hub-router")
                          HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    public void sendDeviceActions(DeviceActionRequest request) {
        log.info("Sending action to hub {} for scenario {}", request.getHubId(), request.getScenarioName());
        hubRouterClient.handleDeviceAction(request);
    }
}
