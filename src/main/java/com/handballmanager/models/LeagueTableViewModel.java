package com.handballmanager.models;

public class LeagueTableViewModel {

    private final int position;
    private final String teamName;
    private final int points;
    private final int goalsScored;
    private final int goalsAgainst;
    private final int wins;
    private final int losses;
    private final int draws;

    public LeagueTableViewModel(int position, String teamName, int points, int goalsScored, int goalsAgainst, int wins, int losses, int draws) {
        this.position = position;
        this.teamName = teamName;
        this.points = points;
        this.goalsScored = goalsScored;
        this.goalsAgainst = goalsAgainst;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
    }

    public int getPosition() {
        return position;
    }

    public String getTeamName() {
        return teamName;
    }

    public int getPoints() {
        return points;
    }

    public int getGoalsScored() {
        return goalsScored;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getDraws() {
        return draws;
    }
}
