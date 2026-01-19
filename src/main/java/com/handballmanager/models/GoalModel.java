package com.handballmanager.models;

import java.time.LocalDateTime;

public class GoalModel {

    private int id;
    private int match_id;
    private int team_id;
    private long time;
    private LocalDateTime goal_time;

    public GoalModel(int match_id, int team_id, long time, LocalDateTime goal_time) {
        this.match_id = match_id;
        this.team_id = team_id;
        this.time = time;
        this.goal_time = goal_time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getMatch_id() {
        return match_id;
    }

    public void setMatch_id(int match_id) {
        this.match_id = match_id;
    }

    public int getTeam_id() {
        return team_id;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public LocalDateTime getGoal_time() {
        return goal_time;
    }

    public void setGoal_time(LocalDateTime goal_time) {
        this.goal_time = goal_time;
    }
}
