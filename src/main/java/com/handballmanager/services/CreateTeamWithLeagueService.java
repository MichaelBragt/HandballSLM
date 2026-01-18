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


    public void createTeamWithLeague(String name) {

        Connection conn = DBConnect.UNIQUE_CONNECT.getConnection();

        try {
            conn.setAutoCommit(false);

            TeamModel team = new TeamModel(name);
            teamDAO.createWithConn(conn, team);

            LeagueModel league = new LeagueModel(team.getId(), league_id);
            leagueDAO.createWithConn(conn, league);

            conn.commit();
        }
        catch (Exception e) {
            try {
                conn.rollback();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Failed to create team with league spot", e);
        }
        finally {
            try {
                conn.setAutoCommit(true);
            } catch (Exception ignored) {}
        }
    }
}
