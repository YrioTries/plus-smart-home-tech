package ru.yandex.practicum.grpc;

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

    public AnalyzerClient(@GrpcClient("hub-router")
                          @Autowired(required = false)
                          HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
        if (hubRouterClient == null) {
            log.warn("gRPC клиент не инициализирован - hub-router не доступен. Analyzer будет работать без отправки команд.");
        } else {
            log.info("gRPC клиент успешно инициализирован");
        }
    }

    public void sendDeviceActions(DeviceActionRequest request) {
        if (hubRouterClient == null) {
            log.warn("gRPC клиент не доступен, пропускаем отправку действия для хаба {}", request.getHubId());
            return;
        }
        log.info("Sending action to hub {} for scenario {}", request.getHubId(), request.getScenarioName());
        hubRouterClient.handleDeviceAction(request);
    }
}