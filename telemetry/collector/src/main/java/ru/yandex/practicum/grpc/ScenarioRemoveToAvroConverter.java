package ru.yandex.practicum.grpc;

import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEvent;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEvent;

@Component
public class ScenarioRemoveToAvroConverter {

    public HubEvent convertToAvro(ru.yandex.practicum.grpc.telemetry.messages.HubEventProto proto) {
        Timestamp protoTimestamp = proto.getTimestamp();
        //Instant instant = Instant.ofEpochSecond(protoTimestamp.getSeconds(), protoTimestamp.getNanos());

        ScenarioRemovedEvent scenarioRemovedEventAvro = convert(proto.getScenarioRemoved());

        return HubEvent.newBuilder()
                .setHubId(proto.getHubId())
                .setTimestamp(protoTimestamp.getSeconds())
                .setPayload(scenarioRemovedEventAvro)
                .build();
    }

    private ScenarioRemovedEvent convert(ru.yandex.practicum.grpc.telemetry.messages.ScenarioRemovedEventProto proto) {
        return ScenarioRemovedEvent.newBuilder()
                .setName(proto.getName())
                .build();
    }
}
