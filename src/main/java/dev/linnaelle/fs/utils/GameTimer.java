package dev.linnaelle.fs.utils;

public class GameTimer {
    private long startTime;
    private long elapsedTime;

    public GameTimer() {
        this.startTime = System.currentTimeMillis();
        this.elapsedTime = 0;
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public void stop() {
        this.elapsedTime = System.currentTimeMillis() - this.startTime;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void reset() {
        this.startTime = System.currentTimeMillis();
        this.elapsedTime = 0;
    }
}
