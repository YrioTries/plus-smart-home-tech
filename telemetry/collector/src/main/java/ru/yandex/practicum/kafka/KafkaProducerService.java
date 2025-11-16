package ru.yandex.practicum.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendSensorEvent(String key, Object event) {
        try {
            log.info("Отправка события датчика в Kafka: {}", event);
            byte[] avroBytes = convertAvroToBytes((SpecificRecord) event);
            kafkaTemplate.send("telemetry.sensors.v1", key, avroBytes);
            log.info("Событие датчика успешно отправлено");
        } catch (Exception e) {
            log.error("Не удалось отправить событие датчика в Kafka", e);
        }
    }

    public void sendHubEvent(String key, Object event) {
        try {
            log.info("Отправка события хаба в Kafka: {}", event);
            byte[] avroBytes = convertAvroToBytes((SpecificRecord) event);
            kafkaTemplate.send("telemetry.hubs.v1", key, avroBytes);
            log.info("Событие хаба успешно отправлено");
        } catch (Exception e) {
            log.error("Не удалось отправить событие хаба в Kafka", e);
        }
    }

    private byte[] convertAvroToBytes(SpecificRecord record) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        DatumWriter<SpecificRecord> writer = new SpecificDatumWriter<>(record.getSchema());

        writer.write(record, encoder);
        encoder.flush();
        out.close();

        return out.toByteArray();
    }
}
