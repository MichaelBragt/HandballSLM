package com.handballmanager.controllers;

import com.handballmanager.models.MatchEvent;
import com.handballmanager.models.MatchModel;
import com.handballmanager.services.MatchReportService;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.util.Map;

public class MatchReportController {
    @FXML public Button backButton;
    @FXML public TableView<MatchEvent> matchReportTable;
    @FXML public TableColumn<MatchEvent, String> eventDetail;
    @FXML public TableColumn<MatchEvent, Number> eventTime;
    @FXML public TableColumn<MatchEvent, String> eventTeam;
    @FXML public VBox reportTab;
    @FXML public Text leftSideTeam;
    @FXML public Text leftSideScore;
    @FXML public Text rightSideTeam;
    @FXML public Text rightSideScore;
    public VBox topLeftBox;
    public VBox topMidBox;
    public VBox topRightBox;
    public HBox topBox;
    private MatchModel match;
    private MainController mainController;
    private final MatchReportService reportService = new MatchReportService();

    /**
     * JavaFX initialize method
     */
    public void initialize() {
        // we want our top box to have equal width for the 3 inner boxes, we do that here
        topLeftBox.prefWidthProperty().bind(topBox.widthProperty().divide(3));
        topMidBox.prefWidthProperty().bind(topBox.widthProperty().divide(3));
        topRightBox.prefWidthProperty().bind(topBox.widthProperty().divide(3));
    }

    /**
     * Method to handle go back button, since we have access to mainController
     * we call mainController so set content back to matchView
     * @param event
     */
    public void goBack(ActionEvent event) {
        mainController.loadTabPage2();
    }

    /**
     * Method to set match object to the one we want to see
     * @param match
     */
    public void setMatch(MatchModel match) {
        this.match = match;
        loadReport(match);
    }

    /**
     * method to recieve mainController
     * @param mainController
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * method to load and show the report for the specific match
     * @param match
     */
    private void loadReport(MatchModel match) {

        ObservableList<MatchEvent> matchEvents = FXCollections.observableList(reportService.fetchMatchReport(match.getId()));

        Map<String, Integer> goals = reportService.countGoals(matchEvents);
        int team1_goals = goals.getOrDefault(match.getTeam1().getName(), 0);
        int team2_goals = goals.getOrDefault(match.getTeam2().getName(), 0);

        leftSideTeam.setText(match.getTeam1().getName());
        rightSideTeam.setText(match.getTeam2().getName());
        leftSideScore.setText(team1_goals + "");
        rightSideScore.setText(team2_goals + "");


        eventDetail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEventType()));
        eventTime.setCellValueFactory(data -> new SimpleLongProperty(data.getValue().getEventTime()));
        eventTeam.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEventTeam()));

        matchReportTable.setItems(matchEvents);
    }
}
