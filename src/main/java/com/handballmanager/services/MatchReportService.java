package com.handballmanager.services;

import com.handballmanager.dataAccesObjects.GoalDAO;
import com.handballmanager.dataAccesObjects.MatchDAO;
import com.handballmanager.dataAccesObjects.PenaltyDAO;
import com.handballmanager.models.MatchEvent;
import com.handballmanager.models.MatchModel;

import java.util.*;

public class MatchReportService {

    private final MatchDAO matchDAO = new MatchDAO();
    private final GoalDAO goalDAO = new GoalDAO();
    private final PenaltyDAO penaltyDAO = new PenaltyDAO();

    /**
     * method to fetch what we need for a report for a specific match
     * we create a list with MatchEvent objects
     * we get all goals and penalties and add them all to our list
     * and then we sort the whole list by event time
     * @param match_id
     * @return
     */
    public List<MatchEvent> fetchMatchReport(int match_id) {

        List<MatchEvent> matchesReport = new ArrayList<>();

        matchesReport.addAll(goalDAO.getGoalsFromMatch(match_id));
        matchesReport.addAll(penaltyDAO.getPenaltiesFromMatch(match_id));

        matchesReport.sort(Comparator.comparingLong(MatchEvent::getEventTime));

        return matchesReport;
    }

    /**
     * helper method to count each teams goal for the report page
     * so we can show the score for each team
     * @param events
     * @return
     */
    public Map<String, Integer> countGoals(List<MatchEvent> events) {

        Map<String, Integer> goals = new HashMap<>();

        for(MatchEvent event : events) {
            if("Mål".equals((event.getEventType()))) {
                goals.merge(event.getEventTeam(), 1, Integer::sum);
            }
        }
        return goals;
    }
}
