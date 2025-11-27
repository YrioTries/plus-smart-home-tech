package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.HubEventProcessor;
import ru.yandex.practicum.kafka.SnapshotProcessor;

@SpringBootApplication
public class AnalyzerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnalyzerApplication.class, args);
    }

    @Slf4j
    @Component
    @RequiredArgsConstructor
    public static class KafkaProcessorsStarter {

        private final HubEventProcessor hubEventProcessor;
        private final SnapshotProcessor snapshotProcessor;

        @EventListener(ApplicationReadyEvent.class)
        public void startAllProcessors() {
            log.info("Starting all Kafka processors...");

            // Запускаем HubEventProcessor
            Thread hubEventThread = new Thread(hubEventProcessor, "HubEventProcessor-Thread");
            hubEventThread.setDaemon(true);
            hubEventThread.start();
            log.info("HubEventProcessor started");

            // Запускаем SnapshotProcessor
            Thread snapshotThread = new Thread(snapshotProcessor, "SnapshotProcessor-Thread");
            snapshotThread.setDaemon(true);
            snapshotThread.start();
            log.info("SnapshotProcessor started");

            // Добавляем shutdown hook
            addShutdownHook();

            log.info("All Kafka processors started successfully");
        }

        private void addShutdownHook() {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Starting graceful shutdown...");

                // Останавливаем процессоры
                hubEventProcessor.shutdown();
                snapshotProcessor.shutdown();

                log.info("Graceful shutdown initiated");
            }));
        }
    }
}