package com.handballmanager.models;

public enum MatchStatus {

    NOT_STARTED,
    RUNNING,
    FINISHED,
    UNKNOWN;

    public static MatchStatus fromDb(String value) {
        if(value == null) {
            return UNKNOWN;
        }

        try {
            return MatchStatus.valueOf(value.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

}
