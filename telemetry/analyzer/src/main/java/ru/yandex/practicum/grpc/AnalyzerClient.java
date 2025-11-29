package ru.yandex.practicum.grpc;

import net.devh.boot.grpc.client.inject.GrpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.messages.DeviceActionRequest;

@Slf4j
@Service
public class AnalyzerClient {
    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public void sendDeviceActions(DeviceActionRequest request) {
        if (hubRouterClient == null) {
            log.warn("gRPC недоступен, пропускаю: {}", request.getScenarioName());
            return;
        }
        try {
            log.info("🚀 Отправляю gRPC: hub={} scenario={}", request.getHubId(), request.getScenarioName());
            hubRouterClient.handleDeviceAction(request);
            log.info("✅ gRPC отправлено: hub={} scenario={}", request.getHubId(), request.getScenarioName());
        } catch (Exception e) {
            log.error("❌ gRPC ОШИБКА для {}: {}", request.getScenarioName(), e.getMessage());
        }
    }
}
