package ru.yandex.practicum.grpc;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.dto.hub.HubEventDto;
import ru.yandex.practicum.dto.sensor.SensorEventDto;
import ru.yandex.practicum.grpc.converter.hub.HubProtoToAvroConverter;
import ru.yandex.practicum.grpc.converter.ProtoToModelConverter;
import ru.yandex.practicum.grpc.converter.sensor.SensorProtoToAvroConverter;
import ru.yandex.practicum.grpc.telemetry.messages.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.messages.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.services.CollectorControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.service.CollectorEventService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final SensorProtoToAvroConverter protoToAvroConverter;
    private final HubProtoToAvroConverter hubProtoToAvroConverter;
    private final CollectorEventService collectorService;
    private final ProtoToModelConverter protoToModelConverter;


    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            SensorEventDto sensorEventDto = protoToModelConverter.convertToModel(request);
            collectorService.processSensorEvent(sensorEventDto);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info(request.toString());
            HubEventDto hubEventDto = protoToModelConverter.convertToModel(request);
            collectorService.processHubEvent(hubEventDto);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    private SensorEventAvro getSensorAvroObject(SensorEventProto request) {
        return switch (request.getPayloadCase()) {
            case MOTION_SENSOR -> protoToAvroConverter.convertToMotionAvro(request);
            case TEMPERATURE_SENSOR -> protoToAvroConverter.convertToTemperatureAvro(request);
            case LIGHT_SENSOR -> protoToAvroConverter.convertToLightAvro(request);
            case CLIMATE_SENSOR -> protoToAvroConverter.convertToClimateAvro(request);
            case SWITCH_SENSOR -> protoToAvroConverter.convertToSwitchAvro(request);
            default -> throw new IllegalArgumentException("Unknown payload type: " + request.getPayloadCase());
        };
    }

    private HubEventAvro getHubAvroObject(HubEventProto request) {
        return switch (request.getPayloadCase()) {
            case DEVICE_ADDED -> hubProtoToAvroConverter.convertToDeviceAdded(request);
            case DEVICE_REMOVED -> hubProtoToAvroConverter.convertToDeviceRemove(request);
            case SCENARIO_ADDED -> hubProtoToAvroConverter.convertToScenarioAdded(request);
            case SCENARIO_REMOVED -> hubProtoToAvroConverter.convertToScenarioRemove(request);
            default -> throw new IllegalArgumentException("Unknown payload type: " + request.getPayloadCase());
        };
    }
}