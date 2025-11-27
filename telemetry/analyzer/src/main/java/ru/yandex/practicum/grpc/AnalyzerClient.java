package ru.yandex.practicum.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.messages.DeviceActionRequest;

@Slf4j
@Service
public class AnalyzerClient {

    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    @PostConstruct
    public void init() {
        try {
            ManagedChannel channel = ManagedChannelBuilder
                    .forAddress("localhost", 59090)
                    .usePlaintext()
                    .keepAliveWithoutCalls(true)
                    .build();

            hubRouterClient = HubRouterControllerGrpc.newBlockingStub(channel);
            log.info("gRPC клиент ручной инициализации ОК!");

        } catch (Exception e) {
            log.error("gRPC подключение НЕТ: {}", e.getMessage());
            this.hubRouterClient = null;
        }
    }

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
