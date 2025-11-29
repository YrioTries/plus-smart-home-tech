package ru.yandex.practicum;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.messages.DeviceActionRequest;

@Slf4j
@GrpcService
public class HubRouterControllerImpl extends HubRouterControllerGrpc.HubRouterControllerImplBase {

    @Override
    public void handleDeviceAction(DeviceActionRequest request,
                                   StreamObserver<Empty> responseObserver) {

        log.info("🚀 handleDeviceAction вызван");
        log.info("➡️ Входящие данные: hubId={}, scenarioName={}",
                request.getHubId(), request.getScenarioName());
        log.info("➡️ Action: sensorId={}, type={}, value={}",
                request.getAction().getSensorId(),
                request.getAction().getType(),
                request.getAction().getValue());

        try {
            // Здесь может быть твоя логика обработки команды
            log.info("⚙️ Обрабатываю команду для сенсора {}",
                    request.getAction().getSensorId());

            // Успешный ответ
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
            log.info("✅ Команда успешно обработана и отправлен ответ gRPC");
        } catch (Exception e) {
            log.error("❌ Ошибка при обработке handleDeviceAction", e);
            responseObserver.onError(e);
        }
    }
}
