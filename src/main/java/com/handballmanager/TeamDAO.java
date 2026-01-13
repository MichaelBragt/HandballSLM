package com.handballmanager;

import com.handballmanager.utils.UIErrorReport;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TeamDAO {

    private static final String INSERT = "INSERT INTO Team (name) VALUES (?)";
    private static final String DELETE = "DELETE FROM Team WHERE id = ?";
    private static final String UPDATE = "UPDATE Team SET name = ? WHERE id = ?";
    private static final String SELECT_ALL = "SELECT * FROM Teams";

    public void create(TeamModel team) {
        // Try with resource, this is a safe way to use statements, as it auto closes after it is done
        // Syntax is: try() {}
        // this syntax returns the id of the created row, we need to decide if we need that
        try (PreparedStatement stmt = DBConnect.UNIQUE_CONNECT.getConnection().prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, team.getName());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    team.setId(keys.getInt(1));
                }
            }
        }
        catch (SQLException e) {
            UIErrorReport.showDatabaseError(e);
            throw new RuntimeException("Failed to create team", e);
        }
    }

    public boolean delete(TeamModel team) {
        // Try with resource, this is a safe way to use statements, as it auto closes after it is done
        // Syntax is: try() {}
        // this syntax returns the id of the created row, we need to decide if we need that
        try (PreparedStatement stmt = DBConnect.UNIQUE_CONNECT.getConnection().prepareStatement(DELETE)) {
            stmt.setInt(1, team.getId());
            int rows = stmt.executeUpdate();
            return rows == 1;
        }
        catch (SQLException e) {
            UIErrorReport.showDatabaseError(e);
            throw new RuntimeException("Failed to delete team", e);
        }
    }

    public List<TeamModel> selectAll() {
        List<TeamModel> teams = new ArrayList<>();

        try(PreparedStatement stmt = DBConnect.UNIQUE_CONNECT.getConnection().prepareStatement(SELECT_ALL);
            ResultSet result = stmt.executeQuery();
        ) {
            while(result.next()) {
                teams.add(new TeamModel(
                result.getInt("id"),
                result.getString("name")
                ));
            }

        }
        catch (SQLException e) {
            UIErrorReport.showDatabaseError(e);
            throw new RuntimeException("Failed to load all teams", e);
        }
        return teams;
    }

}
