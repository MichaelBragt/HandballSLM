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

    public List<MatchEvent> fetchMatchReport(int match_id) {

        List<MatchEvent> matchesReport = new ArrayList<>();

        matchesReport.addAll(goalDAO.getGoalsFromMatch(match_id));
        matchesReport.addAll(penaltyDAO.getPenaltiesFromMatch(match_id));

        matchesReport.sort(Comparator.comparingLong(MatchEvent::getEventTime));

        return matchesReport;
    }

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
