package ru.yandex.practicum.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.messages.DeviceActionRequest;

@Slf4j
@Service
public class AnalyzerClient {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public AnalyzerClient() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 59090)
                .usePlaintext()
                .keepAliveWithoutCalls(true)
                .build();

        this.hubRouterClient = HubRouterControllerGrpc.newBlockingStub(channel);
        log.info("gRPC клиент ручной инициализирован: localhost:59090");
    }



    public void sendDeviceActions(DeviceActionRequest request) {
        try {
            log.info("🚀 Отправляю gRPC: hub={} scenario={}", request.getHubId(), request.getScenarioName());
            hubRouterClient.handleDeviceAction(request);
            log.info("✅ gRPC отправлено: hub={} scenario={}", request.getHubId(), request.getScenarioName());
        } catch (Exception e) {
            log.error("❌ gRPC ОШИБКА для {}: {}", request.getScenarioName(), e.getMessage());
        }
    }
}
