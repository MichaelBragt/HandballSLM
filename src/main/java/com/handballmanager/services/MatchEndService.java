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

    public void endMatchCleanUp(MatchModel match, int team1_id, int team2_id, int team1_goals, int team2_goals) {

        Connection conn = DBConnect.UNIQUE_CONNECT.getConnection();

        try {
            conn.setAutoCommit(false);

            matchDAO.endMatch(conn, match);

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

            leagueDAO.updateEndMatch(conn, team1_id, team1_points, team1_goals, team2_goals, team1_wins, team1_losses, draws);
            leagueDAO.updateEndMatch(conn, team2_id, team2_points, team2_goals, team1_goals, team2_wins, team2_losses, draws);

            conn.commit();
        }
        catch (Exception e) {
            try {
                conn.rollback();
            }
            catch (Exception ex) {
                e.printStackTrace();
            }
            throw new RuntimeException("End Match database write failed", e);
        }
        finally {
            try {
                conn.setAutoCommit(true);
            }
            catch (Exception ignored) {}
        }


    }

}
