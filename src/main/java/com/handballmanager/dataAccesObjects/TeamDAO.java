package com.handballmanager.dataAccesObjects;

import com.handballmanager.DBConnect;
import com.handballmanager.models.TeamModel;
import com.handballmanager.utils.UIErrorReport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeamDAO {

    private static final String INSERT = "INSERT INTO Team (name) VALUES (?)";

    //Since teams can have goals, penalties, matches we do a soft delete when user deletes a team
    // so we set active to 0
    private static final String DELETE = "UPDATE Team SET active = 0 WHERE id = ?";
    private static final String UPDATE = "UPDATE Team SET name = ? WHERE id = ?";
    private static final String SELECT_ALL = "SELECT * FROM Team WHERE active = 1";

    /**
     * Method for creating Team in database
     * @param team
     */
    public void create(TeamModel team) {
        // Try with resource, this is a safe way to use statements, as it auto closes after it is done
        // Syntax is: try() {}
        // this syntax returns the id of the created row, we need to decide if we need that
        // NOT USED ANYMORE we use createWithConn
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

    /**
     * method to create team with connection parameter
     * we need this to be able to rollback create team with league in
     * service layers, they need the same connection to be able to rollback on errors
     * @param conn
     * @param team
     */
    public void createWithConn(Connection conn, TeamModel team) {
        // Try with resource, this is a safe way to use statements, as it auto closes after it is done
        // Syntax is: try() {}
        // this syntax returns the id of the created row, we need to decide if we need that
        try (PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
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

    /**
     * method to update Team name in database
     * @param team
     */
    public void update(TeamModel team) {
        // Try with resource, this is a safe way to use statements, as it auto closes after it is done
        // Syntax is: try() {}
        try (PreparedStatement stmt = DBConnect.UNIQUE_CONNECT.getConnection().prepareStatement(UPDATE)) {
            stmt.setString(1, team.getName());
            stmt.setInt(2, team.getId());
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            UIErrorReport.showDatabaseError(e);
            throw new RuntimeException("Failed to update team", e);
        }
    }

    /**
     * method for deleting Team in database
     * we actually do not delete the row but do a
     * soft-delete and set active to 0
     * @param team
     * @return
     */
    public boolean delete(TeamModel team) {
        // Try with resource, this is a safe way to use statements, as it auto closes after it is done
        // Syntax is: try() {}
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

    /**
     * method for fetching all Teams from database
     * @return
     */
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
