package ru.yandex.practicum.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.messages.DeviceActionRequest;

@Slf4j
@Service
public class AnalyzerClient {

    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public AnalyzerClient(@GrpcClient("hub-router") HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
        if (hubRouterClient == null) {
            log.warn("gRPC клиент не инициализирован - hub-router недоступен. Analyzer будет работать без отправки команд.");
        } else {
            log.info("gRPC клиент успешно инициализирован");
        }
    }

    public void sendDeviceActions(DeviceActionRequest request) {
        if (hubRouterClient == null) {
            log.warn("gRPC клиент недоступен, пропускаем отправку действия для хаба: {}, сценарий: {}, устройство: {}",
                    request.getHubId(),
                    request.getScenarioName(),
                    request.getAction().getSensorId());
            return;
        }

        try {
            log.info("Отправка команды в hub-router: хаб={}, сценарий={}, устройство={}, действие={}",
                    request.getHubId(),
                    request.getScenarioName(),
                    request.getAction().getSensorId(),
                    request.getAction().getType());

            var response = hubRouterClient.handleDeviceAction(request);
            log.debug("Команда успешно отправлена в hub-router: {}", response);

        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.UNAVAILABLE) {
                log.error("Hub Router недоступен на localhost:59090. Убедитесь, что сервис запущен");
            } else {
                log.error("Ошибка при отправке команды в hub-router. Статус: {}, Описание: {}",
                        e.getStatus().getCode(), e.getStatus().getDescription(), e);
            }
        } catch (Exception e) {
            log.error("Неожиданная ошибка при отправке команды в hub-router для хаба: {}, сценарий: {}",
                    request.getHubId(), request.getScenarioName(), e);
        }
    }
}