package dev.imb11.debug;

public final class LogThrottle {
    private final long interval;
    private boolean logged;
    private long last;

    public LogThrottle(long interval) {
        this.interval = interval;
    }

    public boolean ready(long now) {
        if (logged && now - last < interval) {
            return false;
        }
        logged = true;
        last = now;
        return true;
    }

    public void reset(long now) {
        logged = true;
        last = now;
    }
}
