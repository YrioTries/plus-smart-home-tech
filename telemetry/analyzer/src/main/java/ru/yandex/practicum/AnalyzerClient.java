package ru.yandex.practicum;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AnalyzerClient {

    private final ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public AnalyzerClient(@GrpcClient("hub-router")
                          ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    public void sendDeviceActions(ru.yandex.practicum.grpc.telemetry.messages.DeviceActionRequest request) {
        log.info("Sending action to hub {} for scenario {}", request.getHubId(), request.getScenarioName());
        hubRouterClient.handleDeviceAction(request);
    }
}
