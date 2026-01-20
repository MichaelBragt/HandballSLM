package com.handballmanager;

/**
 * Our timelistener interface to helt time listener to notify when timer ticks
 */
public interface TimeListener {

    void onTimeChange(long remainingTime);

}
