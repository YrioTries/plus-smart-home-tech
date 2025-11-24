package ru.yandex.practicum.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.services.CollectorControllerOuterClass;
import ru.yandex.practicum.service.CollectorEventService;


@Slf4j
@GrpcService
@RequiredArgsConstructor
public class EventController extends ru.yandex.practicum.grpc.telemetry.services.CollectorControllerGrpc.CollectorControllerImplBase {

    private final CollectorEventService collectorEventService;
    private final ProtoToModelConverter protoToModelConverter;

    @Override
    public void collectSensorEvent(ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto request, StreamObserver<CollectorControllerOuterClass.CollectResponse> responseObserver) {
        try {
            log.info("Получено gRPC событие датчика типа: {}", request.getPayloadCase());

            var sensorEvent = protoToModelConverter.convertToModel(request);
            collectorEventService.processSensorEvent(sensorEvent);

            var response = CollectorControllerOuterClass.CollectResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Событие датчика успешно обработано")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Ошибка обработки gRPC события датчика: {}", request, e);
            var response = CollectorControllerOuterClass.CollectResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Ошибка обработки: " + e.getMessage())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void collectHubEvent(ru.yandex.practicum.grpc.telemetry.messages.HubEventProto request, StreamObserver<CollectorControllerOuterClass.CollectResponse> responseObserver) {
        try {
            log.info("Получено gRPC событие хаба типа: {}", request.getPayloadCase());

            var hubEvent = protoToModelConverter.convertToModel(request);
            collectorEventService.processHubEvent(hubEvent);

            var response = CollectorControllerOuterClass.CollectResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Событие хаба успешно обработано")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Ошибка обработки gRPC события хаба: {}", request, e);
            var response = CollectorControllerOuterClass.CollectResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Ошибка обработки: " + e.getMessage())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
