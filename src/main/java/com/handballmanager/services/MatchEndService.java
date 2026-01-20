package com.handballmanager.services;

import com.handballmanager.DBConnect;
import com.handballmanager.dataAccesObjects.LeagueDAO;
import com.handballmanager.dataAccesObjects.MatchDAO;
import com.handballmanager.dataAccesObjects.TeamDAO;
import com.handballmanager.models.MatchModel;

import java.sql.Connection;

public class MatchEndService {

    private final LeagueDAO leagueDAO = new LeagueDAO();
    private final MatchDAO matchDAO = new MatchDAO();

    /**
     * Service to do stuff after match is finished
     * @param match
     * @param team1_id
     * @param team2_id
     * @param team1_goals
     * @param team2_goals
     */
    public void endMatchCleanUp(MatchModel match, int team1_id, int team2_id, int team1_goals, int team2_goals) {

        Connection conn = DBConnect.UNIQUE_CONNECT.getConnection();

        // we need rollback features, so we set autocommit to false
        try {
            conn.setAutoCommit(false);

            // we call matchDAO end match method
            matchDAO.endMatch(conn, match);

            // then we calculate how many points, wins, losses and draws each team should get
            int draws = 0;
            int team1_wins = 0;
            int team2_wins = 0;
            int team1_points = 0;
            int team2_points = 0;
            int team1_losses = 0;
            int team2_losses = 0;

            if(team1_goals > team2_goals) {
                team1_wins = 1;
                team1_points = 2;
                team2_losses = 1;
            }
            else if(team1_goals < team2_goals) {
                team2_wins = 1;
                team2_points = 2;
                team1_losses = 1;
            }
            else {
                draws = 1; team1_points = 1; team2_points = 1;
            }

            // and then we update league row for each team
            leagueDAO.updateEndMatch(conn, team1_id, team1_points, team1_goals, team2_goals, team1_wins, team1_losses, draws);
            leagueDAO.updateEndMatch(conn, team2_id, team2_points, team2_goals, team1_goals, team2_wins, team2_losses, draws);

            // commit the changes
            conn.commit();
        }
        catch (Exception e) {
            // if any errors we roll back
            try {
                conn.rollback();
            }
            catch (Exception ex) {
                e.printStackTrace();
            }
            throw new RuntimeException("End Match database write failed", e);
        }
        // and just in case set autocommit back to tru for our connection
        finally {
            try {
                conn.setAutoCommit(true);
            }
            catch (Exception ignored) {}
        }
    }

}
