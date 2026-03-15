package ru.practicum.analyzer.runner;

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

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting Analyzer application...");

        Thread hubEventsThread = new Thread(hubProcessor);
        hubEventsThread.setName("HubEventHandlerThread");
        hubEventsThread.start();

        try {
            snapshotProcessor.run();
        } catch (Exception e) {
            log.error("Fatal error in SnapshotProcessor", e);
            throw e;
        }
    }
}
