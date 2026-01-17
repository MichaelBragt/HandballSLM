package com.handballmanager.controllers;

import com.handballmanager.MatchTimeManager;
import com.handballmanager.dataAccesObjects.GoalDAO;
import com.handballmanager.dataAccesObjects.MatchDAO;
import com.handballmanager.dataAccesObjects.PenaltyDAO;
import com.handballmanager.models.*;
import com.handballmanager.utils.UIErrorReport;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.LocalDateTime;

public class MatchPageController {
    @FXML public TableView<MatchEvent> liveMatchTable;
    @FXML public TableColumn<MatchEvent, String> eventDetail;
    @FXML public TableColumn<MatchEvent, Number> eventTime;
    @FXML public TableColumn<MatchEvent, String> eventTeam;
    @FXML public TableColumn<MatchModel, Void> actions;
    @FXML public Label counter;
    @FXML public HBox topBox;
    @FXML public VBox topLeftBox;
    @FXML public VBox topMidBox;
    @FXML public VBox topRightBox;
    @FXML public BorderPane rootBox;
    @FXML public Text leftSideTeam;
    @FXML public Text rightSideTeam;
    @FXML public Button timerButton;
    @FXML public VBox noMatchText;
    @FXML public Text leftSideScore;
    @FXML public Text rightSideScore;
    @FXML public Button leftTeamGoal;
    @FXML public Button leftTeamPenalty;
    @FXML public Button rightTeamGoal;
    @FXML public Button rightTeamPenalty;

    private MatchModel match;
    private final MatchDAO matchDB = new MatchDAO();
    private MatchEvent matchEvent;
    private final ObservableList<MatchEvent> liveEvents = FXCollections.observableArrayList();
    private MatchTimeManager timer;

    public void setMatch(MatchModel match) {
        // here we set up the initial match IF timer is null
        // we clear the events from former match and set both scores to zero
        // set the button text and counter text to initial values
        if(timer == null) {
            this.match = match;
            liveEvents.clear();
            leftSideScore.setText("0");
            rightSideScore.setText("0");
            timerButton.setText("Start Kamp");
            timerButton.setDisable(false);
            counter.setText("60");
        }
        // if timer is NOT null a match is already running, and we inform the user of this
        else {
            UIErrorReport.showAlert("Kamp igang", "Kamp allerede i gang", "Der er allerede en kamp igang\nVent på den er færdig før\ndu sætter en ny i gang.");
        }
        // update the UI
        updateUI();
    }

    /**
     * method to decide if UI update is for running match or no active match
     */
    private void updateUI() {
        if(match == null) {
            showNoMatch();
        }
        else {
            showActiveMatch();
        }
    }

    /**
     * JavaFX's initialize method that runsa once the controller is loaded
     */
    public void initialize() {

        liveMatchTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );

        liveMatchTable.setItems(liveEvents);
        eventDetail.setCellValueFactory(data ->
            data.getValue().eventTypeProperty()
        );
        eventTime.setCellValueFactory(data ->
            data.getValue().eventTimeProperty()
        );
        eventTeam.setCellValueFactory(data ->
            data.getValue().eventTeamProperty()
        );

        // we want our top box to have equal width for the 3 inner boxes, we do that here
        topLeftBox.prefWidthProperty().bind(rootBox.widthProperty().divide(3));
        topMidBox.prefWidthProperty().bind(rootBox.widthProperty().divide(3));
        topRightBox.prefWidthProperty().bind(rootBox.widthProperty().divide(3));

        eventDetail.setResizable(true);
        eventTime.setResizable(true);
        eventTeam.setResizable(true);
        actions.setResizable(false);

        actions.setMinWidth(40);
        actions.setMaxWidth(40);
        actions.setPrefWidth(40);

        // we haven't a specific onject for our actions column
        // so we create our own cell to show our trashcan icon
        actions.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : "test");
            }
        });
        updateUI();
    }

    /**
     * method for showing UI elements when there is no match running
     */
    private void showNoMatch() {
        counter.setText("--");
        timerButton.setVisible(false);

        leftSideTeam.setText("Team: --");
        rightSideTeam.setText("Team: --");

        rootBox.setCenter(noMatchText);
        noMatchText.setStyle("-fx-font-size: 40px;");
        leftTeamGoal.setDisable(true);
        rightTeamGoal.setDisable(true);
        leftTeamPenalty.setDisable(true);
        rightTeamPenalty.setDisable(true);
    }

    /**
     * helper method to show UI elements on match running
     * also we set our listener up for the game counter here
     */
    private void showActiveMatch() {

        rootBox.setCenter(liveMatchTable);

        leftSideTeam.setText(match.getTeam1().getName());
        rightSideTeam.setText(match.getTeam2().getName());

        timerButton.setManaged(true);
        timerButton.setVisible(true);
    }

    /**
     * method to start/stop and resume a match (the timer)
     * and also set text on buttons according to match state
     * @param event
     */
    public void timerClicked(ActionEvent event) {

        // if timer is not yet set, we set it
        // REMEMBER: This code block is setting up the timer on first click SO
        // after it has set up the 2 listeners, they DO NOT need the button to be clicked again
        // They are running in the background as listeners for tick every second
        if(timer == null) {
            // create an instance of the timer
            timer = new MatchTimeManager();
            // set up the listener so we gets notified on timer value change
            timer.setTickListener(remaining ->
                    // Run the counter.setText on the Java UI thread when possible
                    Platform.runLater(() ->
                            counter.setText(Long.toString(remaining))
                    )

            );
            // set up the second listener that is fired when timer hits zero
            timer.setFinishedCallback(() -> {
                        Platform.runLater(this::resetForNewMatch);
                        matchDB.endMatch(match);
                    }
            );

            // we set the timer to 1 minute and let it run and change button text
            timer.startGameTimer(1);
            leftTeamGoal.setDisable(false);
            rightTeamGoal.setDisable(false);
            leftTeamPenalty.setDisable(false);
            rightTeamPenalty.setDisable(false);
            timerButton.setText("Pause kamp");

            // we set the start_time and status in the database
            matchDB.startMatch(match);

        }
        // else if timer is running we handle pause and resume checks and buttons
        else {
            // if timer is paused (true)
            // we set pause to false and update button text
            if (timer.getPause()) {
                timer.setPause(false);
                timerButton.setText("Pause Kamp");
                leftTeamGoal.setDisable(false);
                rightTeamGoal.setDisable(false);
                leftTeamPenalty.setDisable(false);
                rightTeamPenalty.setDisable(false);

            }
            // else it is NOT paused, and we set pause to true and update button text
            else {
                timer.setPause(true);
                timerButton.setText("Forsæt Kamp");
                leftTeamGoal.setDisable(true);
                rightTeamGoal.setDisable(true);
            }
        }
    }

    /**
     * method to update goal score in UI and database
     * @param event
     */
    public void goal_scored(ActionEvent event) {
        // We get the button clicked so we can know which teams goal button was clicked
        Button clickedButton = (Button) event.getSource();
        TeamModel team_id;

        // we check which teams button was clicked and get the Team object
        if(clickedButton == leftTeamGoal) {
            team_id = match.getTeam1();
        }
        else if (clickedButton == rightTeamGoal) {
            team_id = match.getTeam2();
        }
        else {
            return;
        }
        // goal score time should be how long into the game is was scored
        // so we set time to game length (60) - remaining time
        long time = 60 - timer.getRemainingGameTime();
        // goal time as DateTime object
        LocalDateTime goal_time = LocalDateTime.now();
        // We create a Goal object with our data
        GoalModel goal = new GoalModel(match.getId(), team_id.getId(), time, goal_time);
        // instantiate a GoalDAO object to make DB calls
        GoalDAO goalDAO = new GoalDAO();

        // We wrap the Goal database call in a try/catch because we want to change the goal scoreboard
        // in the UI, but if writing to DB fails it should not do that
        try {
            // insert goal in database
            goalDAO.create(goal);
            // check which teams button was clicked and update the score in the UI
            // We do it this way, so we dont have to make a database call again to get the new score
            if(clickedButton == leftTeamGoal) {
                int score = Integer.parseInt(leftSideScore.getText()) + 1;
                leftSideScore.setText(String.valueOf(score));
            }
            else if (clickedButton == rightTeamGoal) {
                int score = Integer.parseInt(rightSideScore.getText()) + 1;
                rightSideScore.setText(String.valueOf(score));
            }
            else {
                return;
            }
            matchEvent = new MatchEvent("Mål", time, team_id.getName());
            liveEvents.add(matchEvent);
            updateUI();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * method to update goal score in UI and database
     * @param event
     */
    public void penalty(ActionEvent event) {
        // We get the button clicked so we can know which teams goal button was clicked
        Button clickedButton = (Button) event.getSource();
        TeamModel team_id;

        // we check which teams button was clicked and get the Team object
        if(clickedButton == leftTeamPenalty) {
            team_id = match.getTeam1();
        }
        else if(clickedButton == rightTeamPenalty) {
            team_id = match.getTeam2();
        }
        else {
            return;
        }
        // goal score time should be how long into the game is was scored
        // so we set time to game length (60) - remaining time
        long time = 60 - timer.getRemainingGameTime();
        // goal time as DateTime object
        LocalDateTime goal_time = LocalDateTime.now();
        // We create a Goal object with our data
        PenaltyModel penalty = new PenaltyModel(match.getId(), team_id.getId(), time, goal_time);
        // instantiate a GoalDAO object to make DB calls
        PenaltyDAO penaltyDAO = new PenaltyDAO();

        // We wrap the Goal database call in a try/catch because we want to change the goal scoreboard
        // in the UI, but if writing to DB fails it should not do that
        try {
            // insert goal in database
            penaltyDAO.create(penalty);

            matchEvent = new MatchEvent("Rødt kort", time, team_id.getName());
            liveEvents.add(matchEvent);
            updateUI();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * method to call on match end to set state for various things
     */
    private void resetForNewMatch() {
        timerButton.setText("Kamp Slut");
        timerButton.setDisable(true);
        leftTeamGoal.setDisable(true);
        rightTeamGoal.setDisable(true);
        leftTeamPenalty.setDisable(true);
        rightTeamPenalty.setDisable(true);
        timer = null;
        match = null;
    }
}
