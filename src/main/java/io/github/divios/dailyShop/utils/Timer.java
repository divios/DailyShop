package io.github.divios.dailyShop.utils;

import io.github.divios.core_lib.utils.Log;

public class Timer {

    private long start;
    private long end;

    public static Timer create() { return new Timer(); }

    private Timer() {
        start = System.currentTimeMillis();
    }

    public void stop() {
        this.end = System.currentTimeMillis();
    }

    public long getTime() {
        return end - start;
    }

    public void logTime() {
        Log.severe(getTime() + " ms");
    }

    public void stopAndLog() {
        stop();
        logTime();
    }

}
