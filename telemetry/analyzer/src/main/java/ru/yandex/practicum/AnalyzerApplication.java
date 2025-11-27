package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.kafka.HubEventProcessor;
import ru.yandex.practicum.kafka.SnapshotProcessor;


@SpringBootApplication(scanBasePackages = {
        "ru.yandex.practicum",
        "ru.practicum.avro.deserializer"
})
public class AnalyzerApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AnalyzerApplication.class, args);

        final HubEventProcessor hubEventProcessor = context.getBean(HubEventProcessor.class);
        final SnapshotProcessor snapshotProcessor = context.getBean(SnapshotProcessor.class);

        Thread hubEventsThread = new Thread(hubEventProcessor);
        hubEventsThread.setName("HubEventHandlerThread");
        hubEventsThread.setDaemon(true);
        hubEventsThread.start();

        Thread snapshotThread = new Thread(snapshotProcessor);
        snapshotThread.setName("SnapshotProcessorThread");
        hubEventsThread.setDaemon(true);
        snapshotThread.start();

        addShutdownHook(context, hubEventProcessor, snapshotProcessor, hubEventsThread, snapshotThread);
    }

    private static void addShutdownHook(ConfigurableApplicationContext context,
                                        HubEventProcessor hubEventProcessor,
                                        SnapshotProcessor snapshotProcessor,
                                        Thread hubEventsThread,
                                        Thread snapshotThread) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Starting graceful shutdown...");

            // Останавливаем процессоры
            if (hubEventProcessor != null) {
                hubEventProcessor.shutdown();
            }
            if (snapshotProcessor != null) {
                snapshotProcessor.shutdown();
            }

            // Ждем завершения потоков
            try {
                if (hubEventsThread != null && hubEventsThread.isAlive()) {
                    hubEventsThread.join(5000);
                }
                if (snapshotThread != null && snapshotThread.isAlive()) {
                    snapshotThread.join(5000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Закрываем контекст Spring
            context.close();
            System.out.println("Shutdown completed.");
        }));
    }
}