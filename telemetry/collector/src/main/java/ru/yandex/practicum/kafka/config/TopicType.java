package ru.yandex.practicum.kafka.config;

import java.util.List;

public enum TopicType {
    TELEMETRY_SENSORS_V1(KafkaTopics.topicList.get(0)),
    TELEMETRY_HUBS_V1(KafkaTopics.topicList.get(1));

    private final String topic;

    TopicType(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public static boolean isCorrectTopic(String gn) {
        return TopicType.KafkaTopics.topicList.contains(gn);
    }

    private static class KafkaTopics {
        public static final List<String> topicList = List.of("telemetry.sensors.v1", "telemetry.hub.v1");
    }
}