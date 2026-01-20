package com.handballmanager.models;

public class MatchEvent {

    private final int eventId;
    private final String eventType;
    private final long eventTime;
    private final String eventTeam;

    public MatchEvent(int eventId, String eventType, long eventTime, String eventTeam) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.eventTeam = eventTeam;
    }

    public int getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public long getEventTime() {
        return eventTime;
    }

    public String getEventTeam() {
        return eventTeam;
    }




}
