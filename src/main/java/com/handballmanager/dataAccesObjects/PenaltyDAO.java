package com.handballmanager.dataAccesObjects;

import com.handballmanager.DBConnect;
import com.handballmanager.models.PenaltyModel;
import com.handballmanager.utils.UIErrorReport;

import java.sql.*;

public class PenaltyDAO {

    private static final String SELECT = "";
    private static final String SELECT_ALL = "";
    private static final String INSERT = "INSERT INTO Penalty (match_id, team_id, time, penalty_time) VALUES (?,?,?,?) ";
    private static final String SELECT_ALL_FROM_TEAM = "";

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
