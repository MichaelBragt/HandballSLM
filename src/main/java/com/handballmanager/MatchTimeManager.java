package com.handballmanager;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MatchTimeManager {

    private TimeListener listener;

    // Trådsikker executioner i java
    private final ScheduledExecutorService scheuduleCountdown = Executors.newSingleThreadScheduledExecutor();
    // Et tidspunkt på UTC tidslinie (uafhængig af tidszone)
    private Instant endTime;
    // Duration (kan sættes i timer, min, sekunder) volatile gør den trådsikker
    // Så når en tråd ændrer værdien ses det af andre tråde
    private volatile Duration remainingGameTime;
    private volatile boolean pauseCountdown = false;


    public void startGameTimer(long minutes) {
        remainingGameTime = Duration.ofMinutes(minutes);
        endTime = Instant.now().plus(remainingGameTime);

        scheuduleCountdown.scheduleAtFixedRate(() -> {
            // check if pause boolean set, if it is we skip the scheudule runner
            if(pauseCountdown) { return; }

            remainingGameTime = Duration.between(Instant.now(), endTime);

            if(remainingGameTime.isNegative() || remainingGameTime.isZero()) {
                remainingGameTime = Duration.ZERO;
                scheuduleCountdown.shutdown();
            }
            notifyListener(remainingGameTime.toSeconds());
        }, 0, 1, TimeUnit.SECONDS); // 0 delay, 1 køres hvor ofte?, enhed (sekunder)
    }

    public long getRemainingGameTime() {
        return remainingGameTime.toSeconds();
    }

    public void setPause(boolean pause) {
        this.pauseCountdown = pause;
    }

    public void setListener(TimeListener listener) {
        this.listener = listener;
    }

    private void notifyListener(long remaining) {
        if(listener != null) {
            listener.onTimeChange(remaining);
        }
    }

}

