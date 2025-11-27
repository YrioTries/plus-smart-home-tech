package ru.yandex.practicum.grpc;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.messages.DeviceActionRequest;

@Slf4j
@Service
public class AnalyzerClient {

    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    @Autowired
    public AnalyzerClient(@GrpcClient("hub-router")
                          HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    @PostConstruct
    public void init() {
        log.info("AnalyzerClient initialized with gRPC stub: {}", hubRouterClient != null);
    }

    public void sendDeviceActions(DeviceActionRequest request) {
        if (hubRouterClient == null) {
            log.error("gRPC client is null! Check gRPC configuration.");
            return;
        }

        try {
            log.info("Sending action to hub {} for scenario {}", request.getHubId(), request.getScenarioName());
            hubRouterClient.handleDeviceAction(request);
        } catch (Exception e) {
            log.error("Failed to send action to hub {}: {}", request.getHubId(), e.getMessage());
        }
    }
}
