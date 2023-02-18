package life.hanabi.utils;

public class Button {
    private final long lastMs;

    public Button(long lastMs) {
        this.lastMs = lastMs;
    }

    public boolean canBeReduced() {
        return System.currentTimeMillis() - lastMs >= 1000L;
    }

    public long getLastMs() {
        return lastMs;
    }
}