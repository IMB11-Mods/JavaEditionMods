package dev.imb11.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class FeedTrace {
    private static final Logger LOGGER = LoggerFactory.getLogger("glass/projection-renderer");
    private static final long SLOW_STAGE_NANOS = TimeUnit.MILLISECONDS.toNanos(20L);
    private final LogThrottle slowStageLog = new LogThrottle(TimeUnit.SECONDS.toNanos(5L));

    public void slowStage(String stage, long started, Supplier<?> context) {
        long now = System.nanoTime();
        long elapsed = now - started;
        if (elapsed >= SLOW_STAGE_NANOS && slowStageLog.ready(now)) {
            LOGGER.warn("[GLASS projector] slow stage={} durationMs={} {}", stage, elapsed / 1_000_000.0D, context.get());
        }
    }
}
