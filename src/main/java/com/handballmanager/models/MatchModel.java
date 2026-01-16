package com.handballmanager.models;

import java.time.LocalDateTime;

public class MatchModel {

    private int id;
    private TeamModel team1;
    private TeamModel team2;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private MatchStatus status;

    public MatchModel(TeamModel team1, TeamModel team2, LocalDateTime start_time, LocalDateTime end_time, MatchStatus status) {
        this.team1 = team1;
        this.team2 = team2;
        this.start_time = start_time;
        this.end_time = end_time;
        this.status = status;
    }

    public MatchModel(int id, TeamModel team1, TeamModel team2, LocalDateTime start_time, LocalDateTime end_time, MatchStatus status) {
        this.id = id;
        this.team1 = team1;
        this.team2 = team2;
        this.start_time = start_time;
        this.end_time = end_time;
        this.status = status;
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
}
