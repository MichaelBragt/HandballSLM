package com.handballmanager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class MatchTimeManager {

    private TimeListener tickListener;
    private Runnable finishedCallback;

    // Trådsikker executioner i java
    private final ScheduledExecutorService scheuduleCountdown = Executors.newSingleThreadScheduledExecutor();

    // AtomicLong er et trådsikkert tal wrapped so Java selv sørger for alle tråede har
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
                // notify tickListener selvom vi er paused for at sikre værdien kan skrives i UI
                notifyTickListener(remainingGameTime.get());
                return;
            }

            // Sæt variabel og decrement værdien
            long value = remainingGameTime.decrementAndGet();

            // check value has reached 0 and if so stop the scheuduler and notify value 0
            if(value <= 0) {
                remainingGameTime.set(0);
                notifyTickListener(0);
                notifyFinished();
                stop();
                return;
            }
            // notify the tickListener of the value
            notifyTickListener(value);
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

    public void setTickListener(TimeListener tickListener) {
        this.tickListener = tickListener;
    }

    private void notifyTickListener(long remaining) {
        if(tickListener != null) {
            tickListener.onTimeChange(remaining);
        }
    }

    public void setFinishedCallback(Runnable finishedCallback) {
        this.finishedCallback = finishedCallback;
    }

   private void notifyFinished() {
        if(finishedCallback != null) {
            finishedCallback.run();
        }
   }
}

