/* ============================================================
   Database: HandballApp
   Purpose : Live håndbold-app (kampe, mål, udvisninger + liga)
   Notes   : Unicode-ready (danske tegn + emojis)
   ============================================================ */

BEGIN TRY
    IF DB_ID('HandballApp') IS NOT NULL
    BEGIN
        ALTER DATABASE HandballApp SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
        DROP DATABASE HandballApp;
    END

    CREATE DATABASE HandballApp
    COLLATE Danish_Norwegian_CI_AS;
END TRY
BEGIN CATCH
    PRINT 'Fejl ved oprettelse af database';
    THROW;
END CATCH;
GO


/* ------------------------------------------------------------
   Use the database
   ------------------------------------------------------------ */
USE HandballApp;
GO

/* ============================================================
   TABLE: Team
   Purpose: Håndboldhold
   ============================================================ */
BEGIN TRY
    BEGIN TRANSACTION;

    CREATE TABLE Team (
        id INT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL UNIQUE,
        active BIT NOT NULL DEFAULT 1
        -- Soft delete: active = 0 betyder holdet er inaktivt
    );

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO


/* ============================================================
   TABLE: Match
   Purpose: En håndboldkamp mellem to hold
   ============================================================ */
BEGIN TRY
    BEGIN TRANSACTION;

    CREATE TABLE Match (
        id INT IDENTITY(1,1) PRIMARY KEY,

        team_1_id INT NOT NULL,
        team_2_id INT NOT NULL,

        match_length INT NOT NULL,
        start_time DATETIME NULL,
        end_time DATETIME NULL,

        status NVARCHAR(50) NOT NULL,

        CONSTRAINT CK_Match_TeamsDifferent
            CHECK (team_1_id <> team_2_id),

        CONSTRAINT FK_Match_Team1
            FOREIGN KEY (team_1_id) REFERENCES Team(id),

        CONSTRAINT FK_Match_Team2
            FOREIGN KEY (team_2_id) REFERENCES Team(id)
    );

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO


/* ============================================================
   TABLE: Goals
   Purpose: Mål registreret under en kamp
   ============================================================ */
BEGIN TRY
    BEGIN TRANSACTION;

    CREATE TABLE Goals (
        id INT IDENTITY(1,1) PRIMARY KEY,

        match_id INT NOT NULL,
        team_id INT NOT NULL,

        time INT NULL,
        goal_time DATETIME NULL,

        CONSTRAINT FK_Goals_Match
            FOREIGN KEY (match_id) REFERENCES Match(id),

        CONSTRAINT FK_Goals_Team
            FOREIGN KEY (team_id) REFERENCES Team(id)
    );

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO


/* ============================================================
   TABLE: Penalty
   Purpose: Udvisninger under en kamp
   ============================================================ */
BEGIN TRY
    BEGIN TRANSACTION;

    CREATE TABLE Penalty (
        id INT IDENTITY(1,1) PRIMARY KEY,

        match_id INT NOT NULL,
        team_id INT NOT NULL,

        time INT NULL,
        penalty_time DATETIME NULL,

        CONSTRAINT FK_Penalty_Match
            FOREIGN KEY (match_id) REFERENCES Match(id),

        CONSTRAINT FK_Penalty_Team
            FOREIGN KEY (team_id) REFERENCES Team(id)
    );

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO


/* ============================================================
   TABLE: Liga
   Purpose: Stillingen i ligaen (1 liga)
   ============================================================ */
BEGIN TRY
    BEGIN TRANSACTION;

    CREATE TABLE Liga (
        league_id INT NOT NULL,
        team_id INT NOT NULL,

        points INT NOT NULL DEFAULT 0,
        goals_scored INT NOT NULL DEFAULT 0,
        goals_against INT NOT NULL DEFAULT 0,
        wins INT NOT NULL DEFAULT 0,
        losses INT NOT NULL DEFAULT 0,
        draws INT NOT NULL DEFAULT 0,

        CONSTRAINT PK_Liga PRIMARY KEY (team_id),

        CONSTRAINT FK_Liga_Team
            FOREIGN KEY (team_id) REFERENCES Team(id)
    );

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO
