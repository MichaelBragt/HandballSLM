package com.handballmanager;

import com.handballmanager.utils.UIErrorReport;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MatchDAO {

    private static final String INSERT = "INSERT INTO Match (team_1_id, team_2_id, start_time, end_time, status) VALUES (?,?,?,?,?)";
    private static final String DELETE = "DELETE FROM Match WHERE id = ?";
    private static final String UPDATE = "UPDATE Match SET (team_1_id, team_2_id, start_time, end_time, status) = (?,?,?,?,?) WHERE id = ?";
    private static final String SELECT_ALL =
            "SELECT m.id, m.start_time, m.end_time, m.status, t1.id AS team1_id, t1.name AS team1_name, t2.id AS team2_id, t2.name AS team2_name" +
            " FROM Match m" +
            " JOIN Team t1 ON m.team_1_id = t1.id" +
            " JOIN Team t2 ON m.team_2_id = t2.id";

    public List<MatchModel> selectAll() {
        List<MatchModel> matches = new ArrayList<>();

        try(PreparedStatement stmt = DBConnect.UNIQUE_CONNECT.getConnection().prepareStatement(SELECT_ALL);
            ResultSet result = stmt.executeQuery();
        ) {
            while(result.next()) {
                matches.add(new MatchModel(
                        result.getInt("id"),
                        new TeamModel(result.getString("team1_name")),
                        new TeamModel(result.getString("team2_name")),
                        result.getTimestamp("start_time").toLocalDateTime(),
                        result.getTimestamp("end_time").toLocalDateTime(),
                        result.getString("status")
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
