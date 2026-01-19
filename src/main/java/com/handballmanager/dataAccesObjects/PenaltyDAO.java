package com.handballmanager.dataAccesObjects;

import com.handballmanager.DBConnect;
import com.handballmanager.models.MatchEvent;
import com.handballmanager.models.MatchModel;
import com.handballmanager.models.PenaltyModel;
import com.handballmanager.utils.UIErrorReport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PenaltyDAO {

    private static final String SELECT = "";
    private static final String SELECT_ALL = "";
    private static final String DELETE = "DELETE FROM Penalty WHERE id = ?";
    private static final String INSERT = "INSERT INTO Penalty (match_id, team_id, time, penalty_time) VALUES (?,?,?,?) ";
    private static final String SELECT_PENALTIES_FROM_MATCH = "SELECT p.id, p.time, t.name AS team_name FROM Penalty p JOIN Team t ON t.id = p.team_id WHERE p.match_id = ?";
    private static final String SELECT_ALL_FROM_TEAM = "";

    public void delete(int penalty_id) {

        try(
                PreparedStatement stmt = DBConnect.UNIQUE_CONNECT.getConnection().prepareStatement(DELETE)
        ) {
            stmt.setInt(1, penalty_id);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<MatchEvent> getPenaltiesFromMatch(int match_id) {
        List<MatchEvent> matchPenalties = new ArrayList<>();

        try(
                PreparedStatement stmt = DBConnect.UNIQUE_CONNECT.getConnection().prepareStatement(SELECT_PENALTIES_FROM_MATCH);
        ) {
            stmt.setInt(1, match_id);
            ResultSet result = stmt.executeQuery();
            while(result.next()) {
                matchPenalties.add(new MatchEvent(
                        result.getInt("id"),
                        "Rødt Kort",
                        result.getLong("time"),
                        result.getString("team_name")
                ));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return matchPenalties;
    }

    public void create(PenaltyModel penalty) {
        // Try with resource, this is a safe way to use statements, as it auto closes after it is done
        // Syntax is: try() {}
        // this syntax returns the id of the created row, we need to decide if we need that
        try (PreparedStatement stmt = DBConnect.UNIQUE_CONNECT.getConnection().prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, penalty.getMatch_id());
            stmt.setInt(2, penalty.getTeam_id());
            stmt.setLong(3, penalty.getTime());
            stmt.setTimestamp(4, Timestamp.valueOf(penalty.getPenalty_time()));
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    penalty.setId(keys.getInt(1));
                }
            }
        }
        catch (SQLException e) {
            UIErrorReport.showDatabaseError(e);
            throw new RuntimeException("Failed to create penalty", e);
        }
    }

}
