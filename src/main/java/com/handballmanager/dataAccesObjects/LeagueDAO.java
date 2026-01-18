package com.handballmanager.dataAccesObjects;

import com.handballmanager.DBConnect;
import com.handballmanager.models.*;
import com.handballmanager.utils.UIErrorReport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeagueDAO {

    // We use ROW_NUMBER() here to calculate the LEAGUE position in the sql statement and gives it a colum position
    // ROW_NUMBER() assigns a value to the position row and the number it gets assigned it by the OVER (a ruleset)
    // So the rows gets a position value calculated by our 3 ORDER BY sorts
    /**
     * Method to get the League table with position column sorted by the position in the league
     */
    private static final String SELECT_LEAGUE_RANKINGS =
            "SELECT\n" +
            "    ROW_NUMBER() OVER (\n" +
            "        ORDER BY\n" +
            "        l.points DESC,\n" +
            "       (l.goals_scored - l.goals_against) DESC,\n" +
            "        l.goals_scored DESC\n" +
            "    ) AS position,\n" +
            "    t.name AS team_name,\n" +
            "    l.points,\n" +
            "    l.goals_scored,\n" +
            "    l.goals_against,\n" +
            "    l.wins,\n" +
            "    l.draws,\n" +
            "    l.losses\n" +
            "FROM League l\n" +
            "JOIN Team t ON t.id = l.team_id\n" +
            "WHERE l.league_id = 1\n" +
            "AND t.active = 1;\n";
    private static final String SELECT_ALL = "";
    private static final String INSERT = "INSERT INTO League (team_id, league_id) VALUES (?,?) ";
    private static final String UPDATE_END_MATCH = "UPDATE League SET " +
            "points = points + ?, " +
            "goals_scored = goals_scored + ?, " +
            "goals_against = goals_against + ?, " +
            "wins = wins + ?, " +
            "losses = losses + ?, " +
            "draws = draws + ? " +
            "WHERE team_id = ?";


    /**
     * Method to fetch whole League and order by point and then goals - goals taken, and then goals scored
     * @return
     */
    public List<LeagueTableViewModel> getLeague() {

        List<LeagueTableViewModel> league = new ArrayList<>();

        try(
            PreparedStatement stmt = DBConnect.UNIQUE_CONNECT.getConnection().prepareStatement(SELECT_LEAGUE_RANKINGS);
            ResultSet result = stmt.executeQuery();
        ) {
            while(result.next()) {
                league.add(new LeagueTableViewModel (
                        result.getInt("position"),
                        result.getString("team_name"),
                        result.getInt("points"),
                        result.getInt("goals_scored"),
                        result.getInt("goals_against"),
                        result.getInt("wins"),
                        result.getInt("losses"),
                        result.getInt("draws")
                ));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return league;
    }

    /**
     * Method to update league after a match is ended
     * Here we pass in a connection because we have a service method that calls this
     * which also works on other tables, and we need same connection for the service method
     * so we can rollback in cas of errors
     * @param conn
     * @param teamId
     * @param points
     * @param goals_scored
     * @param goals_against
     * @param wins
     * @param losses
     * @param draws
     */
    public void updateEndMatch(Connection conn, int teamId, int points, int goals_scored, int goals_against, int wins, int losses, int draws) {
        // Try with resource, this is a safe way to use statements, as it auto closes after it is done
        // Syntax is: try() {}
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_END_MATCH)) {
            stmt.setInt(1, points);
            stmt.setInt(2, goals_scored);
            stmt.setInt(3, goals_against);
            stmt.setInt(4, wins);
            stmt.setInt(5, losses);
            stmt.setInt(6, draws);
            stmt.setInt(7, teamId);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            UIErrorReport.showDatabaseError(e);
            throw new RuntimeException("Failed to update league row for team", e);
        }
    }

    /**
     * Method to create a League row for a team when a team is created
     * not used anymore, we use createWithConn in our service method
     * @param league
     */
    public void create(LeagueModel league) {
        // Try with resource, this is a safe way to use statements, as it auto closes after it is done
        // Syntax is: try() {}
        try (PreparedStatement stmt = DBConnect.UNIQUE_CONNECT.getConnection().prepareStatement(INSERT)) {
            stmt.setInt(1, league.getTeam_id());
            stmt.setInt(2, league.getLeague_id());

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            UIErrorReport.showDatabaseError(e);
            throw new RuntimeException("Failed to create league team row", e);
        }
    }

    /**
     * Method to create a league row for a newly created team
     * @param conn
     * @param league
     */
    public void createWithConn(Connection conn, LeagueModel league) {
        // Try with resource, this is a safe way to use statements, as it auto closes after it is done
        // Syntax is: try() {}
        try (PreparedStatement stmt = conn.prepareStatement(INSERT)) {
            stmt.setInt(1, league.getTeam_id());
            stmt.setInt(2, league.getLeague_id());

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            UIErrorReport.showDatabaseError(e);
            throw new RuntimeException("Failed to create league team row", e);
        }
    }
}
