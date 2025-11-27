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

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public void sendDeviceActions(DeviceActionRequest request) {
        if (hubRouterClient == null) {
            log.error("gRPC клиент не инициализирован! Проверьте конфигурацию hub-router");
            return;
        }

        try {
            log.info("Отправляю команду в hub-router: хаб={}, сценарий={}, устройство={}, действие={}",
                    request.getHubId(),
                    request.getScenarioName(),
                    request.getAction().getSensorId(),
                    request.getAction().getType());

            var response = hubRouterClient.handleDeviceAction(request);
            log.info("Команда успешно отправлена в hub-router: {}", response);

        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.UNAVAILABLE) {
                log.error("Hub Router недоступен на localhost:59090. Убедитесь, что сервис запущен");
            } else {
                log.error("Ошибка при отправке команды в hub-router: {}", e.getStatus(), e);
            }
        } catch (Exception e) {
            log.error("Неожиданная ошибка при отправке команды в hub-router", e);
        }
    }
}