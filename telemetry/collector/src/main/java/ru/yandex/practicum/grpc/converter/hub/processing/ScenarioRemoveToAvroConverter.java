package ru.yandex.practicum.grpc.converter.hub.processing;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Component
public class ScenarioRemoveToAvroConverter {

    public HubEventAvro convertToAvro(ru.yandex.practicum.grpc.telemetry.messages.HubEventProto proto) {
        Timestamp protoTimestamp = proto.getTimestamp();
        //Instant instant = Instant.ofEpochSecond(protoTimestamp.getSeconds(), protoTimestamp.getNanos());

        ScenarioRemovedEventAvro scenarioRemovedEventAvro = convert(proto.getScenarioRemoved());

        return HubEventAvro.newBuilder()
                .setHubId(proto.getHubId())
                .setTimestamp(protoTimestamp.getSeconds() + protoTimestamp.getNanos())
                .setPayload(scenarioRemovedEventAvro)
                .build();
    }

    private ScenarioRemovedEventAvro convert(ru.yandex.practicum.grpc.telemetry.messages.ScenarioRemovedEventProto proto) {
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(proto.getName())
                .build();
    }
}
