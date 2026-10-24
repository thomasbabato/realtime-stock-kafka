package com.thomasbabato.storesimulator;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

/**
 * Starts one repeating task per store. Each task creates a movement
 * for its store every interval-ms milliseconds.
 */
@Component
public class StoreScheduler {

    private static final Logger log = LoggerFactory.getLogger(StoreScheduler.class);

    private final TaskScheduler scheduler;
    private final JdbcTemplate jdbc;
    private final MovementGenerator generator;
    private final SimulatorProperties props;

    public StoreScheduler(TaskScheduler scheduler, JdbcTemplate jdbc,
                          MovementGenerator generator, SimulatorProperties props) {
        this.scheduler = scheduler;
        this.jdbc = jdbc;
        this.generator = generator;
        this.props = props;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startStores() {
        List<Integer> storeIds = jdbc.queryForList("SELECT id FROM stores ORDER BY id", Integer.class);
        Duration interval = Duration.ofMillis(props.intervalMs());

        for (int storeId : storeIds) {
            // Random start so the stores don't all write at the same moment
            long offsetMs = ThreadLocalRandom.current().nextLong(props.intervalMs());
            scheduler.scheduleAtFixedRate(
                    () -> generator.generate(storeId),
                    Instant.now().plusMillis(offsetMs),
                    interval);
        }

        log.info("Started {} stores, one movement every {} ms each", storeIds.size(), props.intervalMs());
    }
}