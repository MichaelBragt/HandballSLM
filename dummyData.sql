USE HandballApp;
GO

/* ============================================================
   INSERT TEAMS (20 teams)
   ============================================================ */
INSERT INTO Team (name) VALUES
(N'Tømmer Tæverne'),
(N'Målmaskinerne'),
(N'Svedige Albuer'),
(N'KetchUp Kings'),
(N'Bold & Ballade'),
(N'Håndbold Hooligans'),
(N'Netbusters'),
(N'Kampklare Køer'),
(N'Overtræksheltene'),
(N'Slappe Skud'),
(N'Pivskiverne'),
(N'Flyvende Finter'),
(N'Bænkvarmerne'),
(N'Sved & Sejr'),
(N'Halvlegens Helte'),
(N'Boldbanditterne'),
(N'Gulvskrubberne'),
(N'Kontra Kongerne'),
(N'Måltyvene'),
(N'Fløjspillerne');
GO


/* ============================================================
   INSERT LEAGUE ROWS
   ============================================================ */
INSERT INTO League (league_id, team_id)
SELECT 1, id FROM Team;
GO


/* ============================================================
   INSERT MATCHES (20 matches – each team plays twice)
   ============================================================ */
INSERT INTO Match (team_1_id, team_2_id, match_length, start_time, end_time, status)
VALUES
(1, 2, 60, GETDATE(), GETDATE(), 'FINISHED'),
(3, 4, 60, GETDATE(), GETDATE(), 'FINISHED'),
(5, 6, 60, GETDATE(), GETDATE(), 'FINISHED'),
(7, 8, 60, GETDATE(), GETDATE(), 'FINISHED'),
(9, 10, 60, GETDATE(), GETDATE(), 'FINISHED'),
(11, 12, 60, GETDATE(), GETDATE(), 'FINISHED'),
(13, 14, 60, GETDATE(), GETDATE(), 'FINISHED'),
(15, 16, 60, GETDATE(), GETDATE(), 'FINISHED'),
(17, 18, 60, GETDATE(), GETDATE(), 'FINISHED'),
(19, 20, 60, GETDATE(), GETDATE(), 'FINISHED'),

(1, 3, 60, GETDATE(), GETDATE(), 'FINISHED'),
(2, 4, 60, GETDATE(), GETDATE(), 'FINISHED'),
(5, 7, 60, GETDATE(), GETDATE(), 'FINISHED'),
(6, 8, 60, GETDATE(), GETDATE(), 'FINISHED'),
(9, 11, 60, GETDATE(), GETDATE(), 'FINISHED'),
(10, 12, 60, GETDATE(), GETDATE(), 'FINISHED'),
(13, 15, 60, GETDATE(), GETDATE(), 'FINISHED'),
(14, 16, 60, GETDATE(), GETDATE(), 'FINISHED'),
(17, 19, 60, GETDATE(), GETDATE(), 'FINISHED'),
(18, 20, 60, GETDATE(), GETDATE(), 'FINISHED');
GO


/* ============================================================
   INSERT GOALS (handball-realistic, min 20 goals per match)
   ============================================================ */

DECLARE @matchId INT = 1;

WHILE @matchId <= 20
BEGIN
    /* Total goals per match: 20–60 */
    DECLARE @totalGoals INT = 20 + ABS(CHECKSUM(NEWID())) % 41;

    /* Split goals between teams */
    DECLARE @team1Goals INT = @totalGoals / 2
                              + (ABS(CHECKSUM(NEWID())) % 5) - 2;

    /* Safety clamp */
    IF @team1Goals < 5 SET @team1Goals = 5;
    IF @team1Goals > @totalGoals - 5 SET @team1Goals = @totalGoals - 5;

    DECLARE @team2Goals INT = @totalGoals - @team1Goals;

    /* Insert goals for team 1 */
    INSERT INTO Goals (match_id, team_id, time, goal_time)
    SELECT
        m.id,
        m.team_1_id,
        rn * 2,
        DATEADD(SECOND, rn * 15, GETDATE())
    FROM Match m
    CROSS JOIN (
        SELECT TOP (@team1Goals)
            ROW_NUMBER() OVER (ORDER BY NEWID()) AS rn
        FROM sys.objects
    ) g
    WHERE m.id = @matchId;

    /* Insert goals for team 2 */
    INSERT INTO Goals (match_id, team_id, time, goal_time)
    SELECT
        m.id,
        m.team_2_id,
        rn * 2,
        DATEADD(SECOND, rn * 15, GETDATE())
    FROM Match m
    CROSS JOIN (
        SELECT TOP (@team2Goals)
            ROW_NUMBER() OVER (ORDER BY NEWID()) AS rn
        FROM sys.objects
    ) g
    WHERE m.id = @matchId;

    SET @matchId += 1;
END;
GO



/* ============================================================
   INSERT PENALTIES (~3 per match)
   ============================================================ */
INSERT INTO Penalty (match_id, team_id, time, penalty_time)
SELECT
    m.id,
    CASE WHEN ABS(CHECKSUM(NEWID())) % 2 = 0 THEN m.team_1_id ELSE m.team_2_id END,
    10 + ABS(CHECKSUM(NEWID())) % 45,
    GETDATE()
FROM Match m
CROSS JOIN (VALUES (1),(2),(3)) AS p(dummy);
GO


/* ============================================================
   RESET LEAGUE STATS
   ============================================================ */
UPDATE League
SET
    points = 0,
    goals_scored = 0,
    goals_against = 0,
    wins = 0,
    losses = 0,
    draws = 0;
GO


/* ============================================================
   CORRECT LEAGUE CALCULATION (AGGREGATED)
   ============================================================ */
WITH MatchGoals AS (
    SELECT match_id, team_id, COUNT(*) AS goals
    FROM Goals
    GROUP BY match_id, team_id
),
MatchResults AS (
    SELECT
        m.id,
        m.team_1_id,
        ISNULL(g1.goals, 0) AS team1_goals,
        m.team_2_id,
        ISNULL(g2.goals, 0) AS team2_goals
    FROM Match m
    LEFT JOIN MatchGoals g1 ON g1.match_id = m.id AND g1.team_id = m.team_1_id
    LEFT JOIN MatchGoals g2 ON g2.match_id = m.id AND g2.team_id = m.team_2_id
),
TeamAggregates AS (
    SELECT
        team_id,
        SUM(goals_scored)  AS goals_scored,
        SUM(goals_against) AS goals_against,
        SUM(wins)          AS wins,
        SUM(losses)        AS losses,
        SUM(draws)         AS draws,
        SUM(points)        AS points
    FROM (
        SELECT
            team_1_id AS team_id,
            team1_goals AS goals_scored,
            team2_goals AS goals_against,
            CASE WHEN team1_goals > team2_goals THEN 1 ELSE 0 END AS wins,
            CASE WHEN team1_goals < team2_goals THEN 1 ELSE 0 END AS losses,
            CASE WHEN team1_goals = team2_goals THEN 1 ELSE 0 END AS draws,
            CASE
                WHEN team1_goals > team2_goals THEN 2
                WHEN team1_goals = team2_goals THEN 1
                ELSE 0
            END AS points
        FROM MatchResults

        UNION ALL

        SELECT
            team_2_id,
            team2_goals,
            team1_goals,
            CASE WHEN team2_goals > team1_goals THEN 1 ELSE 0 END,
            CASE WHEN team2_goals < team1_goals THEN 1 ELSE 0 END,
            CASE WHEN team2_goals = team1_goals THEN 1 ELSE 0 END,
            CASE
                WHEN team2_goals > team1_goals THEN 2
                WHEN team2_goals = team1_goals THEN 1
                ELSE 0
            END
        FROM MatchResults
    ) s
    GROUP BY team_id
)
UPDATE l
SET
    l.goals_scored  = t.goals_scored,
    l.goals_against = t.goals_against,
    l.wins          = t.wins,
    l.losses        = t.losses,
    l.draws         = t.draws,
    l.points        = t.points
FROM League l
JOIN TeamAggregates t ON t.team_id = l.team_id;
GO


/* ============================================================
   FINAL LEAGUE TABLE (VERIFY)
   ============================================================ */
SELECT
    t.name,
    l.points,
    l.wins,
    l.draws,
    l.losses,
    l.goals_scored,
    l.goals_against,
    (l.goals_scored - l.goals_against) AS goal_difference
FROM League l
JOIN Team t ON t.id = l.team_id
ORDER BY
    l.points DESC,
    goal_difference DESC,
    l.goals_scored DESC;
GO
