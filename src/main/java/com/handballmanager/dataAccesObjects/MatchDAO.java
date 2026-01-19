package com.handballmanager.dataAccesObjects;

import com.handballmanager.DBConnect;
import com.handballmanager.models.MatchModel;
import com.handballmanager.models.TeamModel;
import com.handballmanager.utils.UIErrorReport;
import com.handballmanager.models.MatchStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MatchDAO {

    private static final String INSERT = "INSERT INTO Match (team_1_id, team_2_id, match_length, start_time, end_time, status) VALUES (?,?,?,?,?,?)";
    private static final String DELETE = "DELETE FROM Match WHERE id = ?";
    private static final String UPDATE_START_MATCH = "UPDATE Match set start_time = ?, status = ? WHERE id = ?";
    private static final String UPDATE_END_MATCH = "UPDATE Match set end_time = ?, status = ? WHERE id = ?";
    private static final String UPDATE = "UPDATE Match SET (team_1_id, team_2_id, start_time, end_time, status) = (?,?,?,?,?) WHERE id = ?";
    private static final String SELECT_ALL =
            "SELECT m.id, m.start_time, m.end_time, m.status, t1.id AS team1_id, t1.name AS team1_name, t2.id AS team2_id, t2.name AS team2_name, " +
                    "SUM(CASE WHEN g.team_id = t1.id THEN 1 ELSE 0 END) AS team1_goals, " +
                    "SUM(CASE WHEN g.team_id = t2.id THEN 1 ELSE 0 END) AS team2_goals " +
                    "FROM Match m " +
                    "JOIN Team t1 ON m.team_1_id = t1.id " +
                    "JOIN Team t2 ON m.team_2_id = t2.id " +
                    "LEFT JOIN Goals g ON g.match_id = m.id " +
                    "GROUP BY m.id, m.start_time, m.end_time, m.status, t1.id, t1.name, t2.id, t2.name;";

    public void create(MatchModel match) {
        // Try with resource, this is a safe way to use statements, as it auto closes after it is done
        // Syntax is: try() {}
        // this syntax returns the id of the created row, we need to decide if we need that
        try (PreparedStatement stmt = DBConnect.UNIQUE_CONNECT.getConnection().prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, match.getTeam1().getId());
            stmt.setInt(2, match.getTeam2().getId());
            stmt.setInt(3, 60);
            stmt.setNull(4, Types.TIMESTAMP);
            stmt.setNull(5, Types.TIMESTAMP);
            stmt.setString(6, MatchStatus.NOT_STARTED.name());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    match.setId(keys.getInt(1));
                }
            }
        }
        catch (SQLException e) {
            UIErrorReport.showDatabaseError(e);
            throw new RuntimeException("Failed to create team", e);
        }
    }

    public void startMatch(MatchModel match) {
        // Try with resource, this is a safe way to use statements, as it auto closes after it is done
        // Syntax is: try() {}
        // this syntax returns the id of the created row, we need to decide if we need that
        try (PreparedStatement stmt = DBConnect.UNIQUE_CONNECT.getConnection().prepareStatement(UPDATE_START_MATCH)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(2, MatchStatus.RUNNING.name());
            stmt.setInt(3, match.getId());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            UIErrorReport.showDatabaseError(e);
            throw new RuntimeException("Failed to start match", e);
        }
    }

    public void endMatch(Connection conn, MatchModel match) {
        // Try with resource, this is a safe way to use statements, as it auto closes after it is done
        // Syntax is: try() {}
        // this syntax returns the id of the created row, we need to decide if we need that
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_END_MATCH)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(2, MatchStatus.FINISHED.name());
            stmt.setInt(3, match.getId());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            UIErrorReport.showDatabaseError(e);
            throw new RuntimeException("Failed to end match", e);
        }
    }

    public List<MatchModel> selectAll() {
        List<MatchModel> matches = new ArrayList<>();

        try(PreparedStatement stmt = DBConnect.UNIQUE_CONNECT.getConnection().prepareStatement(SELECT_ALL);
            ResultSet result = stmt.executeQuery();
        ) {
            while(result.next()) {

                MatchStatus status = MatchStatus.fromDb(result.getString("status"));
                Timestamp startTs = result.getTimestamp("start_time");
                Timestamp endTs   = result.getTimestamp("end_time");

                matches.add(new MatchModel(
                        result.getInt("id"),
                        new TeamModel(result.getString("team1_name")),
                        new TeamModel(result.getString("team2_name")),
                        startTs == null ? null : startTs.toLocalDateTime(),
                        endTs == null ? null : endTs.toLocalDateTime(),
                        status,
                        result.getInt("team1_goals"),
                        result.getInt("team2_goals")
                ));
            }
        }
        catch (SQLException e) {
            UIErrorReport.showDatabaseError(e);
            throw new RuntimeException("Failed to load all teams", e);
        }
        return matches;
    }
}
