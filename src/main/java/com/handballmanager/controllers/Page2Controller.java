package com.handballmanager.controllers;

import com.handballmanager.dataAccesObjects.MatchDAO;
import com.handballmanager.dataAccesObjects.TeamDAO;
import com.handballmanager.models.MatchModel;
import com.handballmanager.models.MatchStatus;
import com.handballmanager.models.TeamModel;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import javax.imageio.IIOException;
import java.io.IOException;

public class Page2Controller {

    @FXML
    public TableView<MatchModel> matchTable;
    @FXML
    public TableColumn<MatchModel, String> teamAColumn;
    @FXML
    public TableColumn<MatchModel, String> scoreBoardColumn;
    @FXML
    public TableColumn<MatchModel, String> teamBColumn;
    @FXML
    public Label counter;
    @FXML
    public VBox vBoxContent;

    // Opret Team Data Access Object
    private final TeamDAO teamDAO = new TeamDAO();
    private final MatchDAO matchDAO = new MatchDAO();

    public MainController mainController;

    public Button newMatchButton;

    /**
     * JavaFX initialize
     */
    public void initialize() {

        loadMatches();

        // We set a mouse event listener to listen for doubleclick
        // on the table rows, and load the report for the match
        matchTable.setRowFactory(tv -> {
            TableRow<MatchModel> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    // since we have access to the main controler
                    // we here call loadPage2Report with the specific match object to show
                    // the report for that match
                    mainController.loadPage2Report(row.getItem());
                }
            });

            return row;
        });
    }

    /**
     * method to reload matches on Tab select
     * called in maincontroller when tab 2 is selected
     */
    public void onTabSelected() {
        loadMatches();
    }

    /**
     * method to get access to maincontroller passed in from there
     *
     * @param mainController
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * method to decide what happens when we click "Ny Kamp"
     */
    public void setNewMatchButtonPressed() {

        // Create an observable list with all our teams (selectAll)
        ObservableList<TeamModel> teams = FXCollections.observableArrayList(teamDAO.selectAll());

        // Create a new Dialog and set title
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Ny kamp");

        // create a Start button
        ButtonType opretBtn = new ButtonType("Start", ButtonBar.ButtonData.OK_DONE);
        // add button and a cancel button
        dialog.getDialogPane().getButtonTypes().addAll(opretBtn, ButtonType.CANCEL);

        // Get our OK button as a node, we need it to stop JavaFx from closing the dialog
        // since JavaFX closes a dialog by defautl when ok is clicked, but we need it to
        // stay open in case of user errors
        Node okBtn = dialog.getDialogPane().lookupButton(opretBtn);

        // create a combobox
        ComboBox<TeamModel> teamAlist = new ComboBox<>(teams);
        // make it max width
        teamAlist.setMaxWidth(Double.MAX_VALUE);
        // Set prompt text
        teamAlist.setPromptText("Vælg hold");

        // combobox for second team
        ComboBox<TeamModel> teamBlist = new ComboBox<>(teams);
        teamBlist.setMaxWidth(Double.MAX_VALUE);
        teamBlist.setPromptText("Vælg hold");

        // create VBox to hold our content for our Dialog
        VBox content = new VBox(10);
        Label labelA = new Label("Hjemmehold");
        Label labelB = new Label("Udehold");
        // add it
        content.getChildren().addAll(labelA, teamAlist, labelB, teamBlist);
        // add VBox to our Dialog
        dialog.getDialogPane().setContent(content);

        // add an eventfilter to our OK node we extracted
        // and on errors consume the event so the box don't close
        okBtn.addEventFilter(ActionEvent.ACTION, e -> {

            TeamModel teamA = teamAlist.getValue();
            TeamModel teamB = teamBlist.getValue();

            if (teamA == null || teamB == null) {
                showError("Du skal vælge 2 hold");
                e.consume();
                return;
            }

            if (teamA.equals(teamB)) {
                showError("Et hold kan ikke spille mod sig selv\nVælg 2 forskellige hold");
                e.consume();
                return;
            }

            // If all is okay run this code where we create a new match call the databse and insert it

            MatchModel match = new MatchModel(teamA, teamB, null, null, MatchStatus.NOT_STARTED, 0, 0);
            matchDAO.create(match);
            // and call our method to switch to the live match Tab
            mainController.focusLiveMatchTab(match);
        });
        // show our Dialog and wait
        dialog.showAndWait();
    }

    /**
     * method to load our matches into tableview
     */
    private void loadMatches() {

        ObservableList<MatchModel> matches = FXCollections.observableList(matchDAO.selectAll());

        teamAColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getTeam1().getName()));

        // this column is aReadOnlyWrapper so that we can output our custom string
        scoreBoardColumn.setCellValueFactory(data -> {
            MatchModel match = data.getValue();
            String score = match.getTeam1Goals() + " - " + match.getTeam2Goals();
            return new ReadOnlyStringWrapper(score);
        });

        teamBColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getTeam2().getName()));
//        actions.setCellFactory();
        matchTable.setItems(matches);
    }

    /**
     * error method to show errors
     * @param message
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ugyldigt valg");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
