package ru.yandex.practicum.kafka.serializer;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GeneralAvroSerializer implements Serializer<SpecificRecordBase> {
    private static final Logger log = LoggerFactory.getLogger(GeneralAvroSerializer.class);
    private final EncoderFactory encoderFactory;
    private BinaryEncoder encoder;

    public GeneralAvroSerializer() {
        this.encoderFactory = EncoderFactory.get();
    }

    public GeneralAvroSerializer(EncoderFactory encoderFactory) {
        this.encoderFactory = encoderFactory;
    }

    @Override
    public byte[] serialize(String topic, SpecificRecordBase data) {
        if (data == null) {
            log.warn("⚠️ Attempt to serialize null data for topic: {}", topic);
            return null;
        }

        // Детальное логирование для диагностики
        String className = data.getClass().getSimpleName();
        String schemaName = data.getSchema().getName();
        String fullSchemaName = data.getSchema().getFullName();

        log.info("🔧 SERIALIZATION START ==================================");
        log.info("🔧 Topic: {}", topic);
        log.info("🔧 Java Class: {}", className);
        log.info("🔧 Schema Name: {}", schemaName);
        log.info("🔧 Full Schema: {}", fullSchemaName);
        log.info("🔧 Schema Fields: {}", data.getSchema().getFields());

        // Критическая проверка - то что нам нужно!
        if (!className.equals(schemaName)) {
            log.error("🚨 CRITICAL SCHEMA MISMATCH DETECTED!");
            log.error("🚨 Java Class: '{}'", className);
            log.error("🚨 Avro Schema: '{}'", schemaName);
            log.error("🚨 This indicates wrong Avro schema mapping!");
        } else {
            log.info("✅ Schema and Class names match correctly");
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            encoder = encoderFactory.binaryEncoder(out, encoder);
            DatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>(data.getSchema());
            writer.write(data, encoder);
            encoder.flush();

            byte[] result = out.toByteArray();

            log.info("🔧 Serialized {} bytes for {}", result.length, className);
            log.info("🔧 SERIALIZATION COMPLETE ===========================");

            return result;
        } catch (IOException ex) {
            log.error("❌ SERIALIZATION FAILED for {} in topic {}", className, topic);
            log.error("❌ Error: {}", ex.getMessage());
            log.error("❌ Schema was: {}", schemaName);
            throw new SerializationException("Ошибка сериализации данных для топика [" + topic + "]", ex);
        }
    }
}