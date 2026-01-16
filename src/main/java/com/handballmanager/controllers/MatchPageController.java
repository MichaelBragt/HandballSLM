package com.handballmanager.controllers;

import com.handballmanager.MatchTimeManager;
import com.handballmanager.dataAccesObjects.GoalDAO;
import com.handballmanager.dataAccesObjects.MatchDAO;
import com.handballmanager.models.GoalModel;
import com.handballmanager.models.MatchEvent;
import com.handballmanager.models.MatchModel;
import com.handballmanager.models.TeamModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.util.ArrayList;

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
//    @FXML public VBox rootBox;
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
    private MatchEvent matchEvent;
    private final ObservableList<MatchEvent> liveEvents = FXCollections.observableArrayList();

    private MatchTimeManager timer;

    public void setMatch(MatchModel match) {
        this.match = match;
        timerButton.setText("Start Kamp");
        counter.setText("60");
        updateUI();
    }

    private void updateUI() {
        if(match == null) {
            showNoMatch();
        }
        else {
            showActiveMatch();
        }
    }

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
     * method to star/stop and resume a match (the timer)
     * and also set text on butoon according to what it should say
     * @param event
     */
    public void timerClicked(ActionEvent event) {

        // if timer is not yet set, we set it
        if(timer == null) {
            timer = new MatchTimeManager();
            timer.setListener(remaining ->
                    // Run the counter.setText on the Java UI thread when possible
                    Platform.runLater(() ->
                            counter.setText(Long.toString(remaining))
                    )
            );
            timer.setPause(true);
            timer.startGameTimer(1);
        }

        // if timer is paused (true)
        // we set pause to false and update button text
        if(timer.getPause()) {
            timer.setPause(false);
            timerButton.setText("Pause Kamp");
        }
        // else it is NOT paused and we set pause to true and update button text
        else {
            timer.setPause(true);
            timerButton.setText("Forsæt Kamp");
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
}
