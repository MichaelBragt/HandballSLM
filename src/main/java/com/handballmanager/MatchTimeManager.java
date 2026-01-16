package com.handballmanager;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class MatchTimeManager {

    private TimeListener listener;

    // Trådsikker executioner i java
    private static final ScheduledExecutorService scheuduleCountdown = Executors.newSingleThreadScheduledExecutor();

    // AtomicLong  er et trådsikkert tal wrapped so Java selv sørger for alle tråede har
    // nyeste værdi
    private final AtomicLong remainingGameTime = new AtomicLong();
    private volatile boolean pauseCountdown = false;


    public void startGameTimer(long minutes) {
        // sæt vores minutter gange 60 for at få sekunder
        remainingGameTime.set(minutes * 60);

        // opret vores scheuduler til at køre med fixed rate
        scheuduleCountdown.scheduleAtFixedRate(() -> {
            // check if pause boolean set, if it is we skip the scheudule runner
            if(pauseCountdown) {
                // notify listener selvom vi er paused for at sikre værdien kan skrives i UI
                notifyListener(remainingGameTime.get());
                return;
            }

            // Sæt variabel og decrement værdien
            long value = remainingGameTime.decrementAndGet();

            // check value has reached 0 and if so stop the scheuduler and notify value 0
            if(value <= 0) {
                remainingGameTime.set(0);
                notifyListener(0);
                setPause(true);
                stop();
                return;
            }
            // notify the listener of the value
            notifyListener(value);
        }, 0, 1, TimeUnit.SECONDS); // 0 delay, 1 køres hvor ofte?, enhed (sekunder)
    }

    // Stop sheuduler helper function
    public void stop() {
        scheuduleCountdown.shutdownNow();
    }

    public long getRemainingGameTime() {
        return remainingGameTime.get();
    }

    // set pause value
    public void setPause(boolean pause) {
        this.pauseCountdown = pause;
    }

    // get value of pause
    public boolean getPause() {
        return pauseCountdown;
    }

    public void setListener(TimeListener listener) {
        this.listener = listener;
    }

    private void notifyListener(long remaining) {
        if(listener != null) {
            listener.onTimeChange(remaining);
        }
    }

    public static void shutdownAll() {
        scheuduleCountdown.shutdownNow();
    }

}

