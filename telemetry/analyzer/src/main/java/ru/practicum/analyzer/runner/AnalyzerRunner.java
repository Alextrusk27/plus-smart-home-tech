package ru.practicum.analyzer.runner;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AnalyzerRunner implements CommandLineRunner {
    private final HubProcessor hubProcessor;
    private final SnapshotProcessor snapshotProcessor;

    private Thread hubThread;
    private Thread snapshotThread;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting Analyzer application...");

        hubThread = new Thread(hubProcessor);
        hubThread.setName("HubProcessorThread");
        hubThread.start();
        log.info("HubProcessor started in thread: {}", hubThread.getName());

        snapshotThread = new Thread(snapshotProcessor);
        snapshotThread.setName("SnapshotProcessorThread");
        snapshotThread.start();
        log.info("SnapshotProcessor started in thread: {}", snapshotThread.getName());
    }

    @PreDestroy
    public void shutdown() {
        log.info("Stopping Analyzer application...");

        hubProcessor.shutdown();
        snapshotProcessor.shutdown();

        try {
            if (hubThread != null && hubThread.isAlive()) {
                hubThread.join(5000);
                log.info("HubProcessor stopped");
            }
            if (snapshotThread != null && snapshotThread.isAlive()) {
                snapshotThread.join(5000);
                log.info("SnapshotProcessor stopped");
            }
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for processors to stop", e);
            Thread.currentThread().interrupt();
        }
        log.info("Analyzer application stopped");
    }
}
