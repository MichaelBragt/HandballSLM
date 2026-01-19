package com.handballmanager.dataAccesObjects;

import com.handballmanager.DBConnect;
import com.handballmanager.models.GoalModel;
import com.handballmanager.models.MatchEvent;
import com.handballmanager.models.MatchModel;
import com.handballmanager.models.TeamModel;
import com.handballmanager.utils.UIErrorReport;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GoalDAO {

//    private int match_id;
//    private int team_id;
//    private LocalDateTime time;
//    private int goal_time;

    private static final String DELETE = "DELETE FROM Goals WHERE id = ?";
    private static final String SELECT_GOALS_FROM_MATCH = "SELECT g.id, g.time, t.name AS team_name FROM Goals g JOIN Team t ON t.id = g.team_id WHERE g.match_id = ?";
    private static final String INSERT = "INSERT INTO Goals (match_id, team_id, time, goal_time) VALUES (?,?,?,?) ";
    private static final String SELECT_ALL_FROM_TEAM = "";

    public void delete(int goal_id) {

        try(
                PreparedStatement stmt = DBConnect.UNIQUE_CONNECT.getConnection().prepareStatement(DELETE)
        ) {
            stmt.setInt(1, goal_id);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<MatchEvent> getGoalsFromMatch(int match_id) {
        List<MatchEvent> matchGoals = new ArrayList<>();

        try(
            PreparedStatement stmt = DBConnect.UNIQUE_CONNECT.getConnection().prepareStatement(SELECT_GOALS_FROM_MATCH);

            ) {
                stmt.setInt(1, match_id);
                ResultSet result = stmt.executeQuery();
                while(result.next()) {
                    matchGoals.add(new MatchEvent(
                            result.getInt("id"),
                            "Mål",
                            result.getLong("time"),
                            result.getString("team_name")
                    ));
                }
            }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return matchGoals;
    }

    public void create(GoalModel goal) {
        // Try with resource, this is a safe way to use statements, as it auto closes after it is done
        // Syntax is: try() {}
        // this syntax returns the id of the created row, we need to decide if we need that
        try (PreparedStatement stmt = DBConnect.UNIQUE_CONNECT.getConnection().prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, goal.getMatch_id());
            stmt.setInt(2, goal.getTeam_id());
            stmt.setLong(3, goal.getTime());
            stmt.setTimestamp(4, Timestamp.valueOf(goal.getGoal_time()));
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    goal.setId(keys.getInt(1));
                }
            }
        }
        catch (SQLException e) {
            UIErrorReport.showDatabaseError(e);
            throw new RuntimeException("Failed to create goal", e);
        }

    }

}
