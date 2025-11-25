package ru.yandex.practicum.kafka.kafka.serializer;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import ru.yandex.practicum.kafka.telemetry.event.HubEvent;
import ru.yandex.practicum.kafka.telemetry.event.SensorEvent;

public class GeneralAvroDeserializer implements Deserializer<SpecificRecordBase> {
    private final DecoderFactory decoderFactory = DecoderFactory.get();

    @Override
    public SpecificRecordBase deserialize(String topic, byte[] bytes) {
        try {
            if (bytes != null) {
                BinaryDecoder decoder = decoderFactory.binaryDecoder(bytes, null);

                DatumReader<SpecificRecordBase> reader = switch (topic) {
                    case "telemetry.sensors.v1" -> new SpecificDatumReader<>(SensorEvent.getClassSchema());
                    case "telemetry.hub.v1" -> new SpecificDatumReader<>(HubEvent.getClassSchema());
                    default -> throw new IllegalArgumentException("Неизвестный топик: " + topic);
                };

                // используем название топика для определения класса сообщения и получения схемы

                return reader.read(null, decoder);
            }
            return null;
        } catch (Exception e) {
            throw new SerializationException("Ошибка десереализации данных из топика [" + topic + "]", e);
        }
    }
}