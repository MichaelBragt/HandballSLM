package com.handballmanager.models;

import java.time.LocalDateTime;

public class MatchModel {

    private int id;
    private TeamModel team1;
    private TeamModel team2;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private MatchStatus status;
    private int team1Goals;
    private int team2Goals;

    public MatchModel(TeamModel team1, TeamModel team2, LocalDateTime start_time, LocalDateTime end_time, MatchStatus status, int team1Goals, int team2Goals) {
        this.team1 = team1;
        this.team2 = team2;
        this.start_time = start_time;
        this.end_time = end_time;
        this.status = status;
        this.team1Goals = team1Goals;
        this.team2Goals = team2Goals;
    }

    public MatchModel(int id, TeamModel team1, TeamModel team2, LocalDateTime start_time, LocalDateTime end_time, MatchStatus status, int team1Goals, int team2Goals) {
        this.id = id;
        this.team1 = team1;
        this.team2 = team2;
        this.start_time = start_time;
        this.end_time = end_time;
        this.status = status;
        this.team1Goals = team1Goals;
        this.team2Goals = team2Goals;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    public TeamModel getTeam1() {
        return team1;
    }

//    public void setTeam_1_id(int team_1_id) {
//        this.team_1_id = team_1_id;
//    }

    public TeamModel getTeam2() {
        return team2;
    }

//    public void setTeam_2_id(int team_2_id) {
//        this.team_2_id = team_2_id;
//    }

    public LocalDateTime getStart_time() {
        return start_time;
    }

    public void setStart_time(LocalDateTime start_time) {
        this.start_time = start_time;
    }

    public LocalDateTime getEnd_time() {
        return end_time;
    }

    public void setEnd_time(LocalDateTime end_time) {
        this.end_time = end_time;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public int getTeam1Goals() {
        return team1Goals;
    }

    public void setTeam1Goals(int team1Goals) {
        this.team1Goals = team1Goals;
    }

    public int getTeam2Goals() {
        return team2Goals;
    }

    public void setTeam2Goals(int team2Goals) {
        this.team2Goals = team2Goals;
    }
}
