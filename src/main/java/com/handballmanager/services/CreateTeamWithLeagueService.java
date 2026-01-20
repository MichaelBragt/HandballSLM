package com.handballmanager.services;

import com.handballmanager.DBConnect;
import com.handballmanager.dataAccesObjects.LeagueDAO;
import com.handballmanager.dataAccesObjects.TeamDAO;
import com.handballmanager.models.LeagueModel;
import com.handballmanager.models.TeamModel;

import java.sql.Connection;

public class CreateTeamWithLeagueService {

    private final TeamDAO teamDAO = new TeamDAO();
    private final LeagueDAO leagueDAO = new LeagueDAO();
    private int league_id = 1;


    /**
     * Service methos to create a team and right after create a row for that team in the league
     * @param name
     */
    public void createTeamWithLeague(String name) {

        Connection conn = DBConnect.UNIQUE_CONNECT.getConnection();

        // We need rollback features since we work on 2 tables
        // so we set autocommit to fals
        try {
            conn.setAutoCommit(false);

            TeamModel team = new TeamModel(name);
            teamDAO.createWithConn(conn, team);

            LeagueModel league = new LeagueModel(team.getId(), league_id);
            leagueDAO.createWithConn(conn, league);

            // after both our db calls has run with succes we commit
            conn.commit();
        }
        catch (Exception e) {
            // if there were errors we roll back
            try {
                conn.rollback();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Failed to create team with league spot", e);
        }
        // and just in case we set our connection to autocommit again
        // IF we use it anywhere else, which we should not. But better safe than sorry :-)
        finally {
            try {
                conn.setAutoCommit(true);
            } catch (Exception ignored) {}
        }
    }
}
