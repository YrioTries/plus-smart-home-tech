package ru.yandex.practicum.kafka.kafka.config;

import lombok.Getter;

import java.util.List;

@Getter
public enum TopicType {
    TELEMETRY_SENSORS_V1(KafkaTopics.topicList.get(0)),
    TELEMETRY_HUBS_V1(KafkaTopics.topicList.get(1));

    private final String topic;

    TopicType(String topic) {
        this.topic = topic;
    }

    public static boolean isCorrectTopic(String gn) {
        return KafkaTopics.topicList.contains(gn);
    }

    private static class KafkaTopics {
        public static final List<String> topicList = List.of("telemetry.sensors.v1", "telemetry.hubs.v1");
    }
}