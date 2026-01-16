package com.handballmanager.dataAccesObjects;

import com.handballmanager.DBConnect;
import com.handballmanager.models.GoalModel;
import com.handballmanager.models.TeamModel;
import com.handballmanager.utils.UIErrorReport;

import java.sql.*;
import java.time.LocalDateTime;

public class GoalDAO {

//    private int match_id;
//    private int team_id;
//    private LocalDateTime time;
//    private int goal_time;

    private static final String SELECT = "";
    private static final String SELECT_ALL = "";
    private static final String INSERT = "INSERT INTO Goals (match_id, team_id, time, goal_time) VALUES (?,?,?,?) ";
    private static final String SELECT_ALL_FROM_TEAM = "";

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
