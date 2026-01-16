package com.handballmanager.models;

import javafx.beans.property.*;

public class MatchEvent {

    private final StringProperty eventType = new SimpleStringProperty();
    private final LongProperty eventTime = new SimpleLongProperty();
    private final StringProperty eventTeam = new SimpleStringProperty();

    public MatchEvent(String eventType, long eventTime, String eventTeam) {
        this.eventType.set(eventType);
        this.eventTime.set(eventTime);
        this.eventTeam.set(eventTeam);
    }

    public String getEventType() {
        return eventType.get();
    }

    public StringProperty eventTypeProperty() {
        return eventType;
    }

    public long getEventTime() {
        return eventTime.get();
    }

    public LongProperty eventTimeProperty() {
        return eventTime;
    }

    public StringProperty getEventTeam() {
        return eventTeam;
    }

    public StringProperty eventTeamProperty() {
        return eventTeam;
    }
}
