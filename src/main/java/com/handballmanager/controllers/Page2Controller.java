package com.handballmanager.controllers;

import com.handballmanager.dataAccesObjects.MatchDAO;
import com.handballmanager.dataAccesObjects.TeamDAO;
import com.handballmanager.models.MatchModel;
import com.handballmanager.models.MatchStatus;
import com.handballmanager.models.TeamModel;
import javafx.beans.property.ReadOnlyObjectWrapper;
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

    @FXML public TableView<MatchModel> matchTable;
    @FXML public TableColumn<MatchModel, String> teamAColumn;
    @FXML public TableColumn<MatchModel, String> scoreBoardColumn;
    @FXML public TableColumn<MatchModel, String> teamBColumn;
    @FXML public Label counter;
    @FXML public VBox vBoxContent;

    public MainController mainController;

    public Button newMatchButton;

    public void initialize() {


        loadMatches();

        // We set a mouse event listener to listen for doubleclick
        // on the table rows, and load the report for the match
        matchTable.setRowFactory(tv -> {
            TableRow<MatchModel> row = new TableRow<>();

            row.setOnMouseClicked( event -> {
                if(event.getClickCount() == 2 && !row.isEmpty()) {
                    mainController.loadPage2Report(row.getItem());
                }
            });

            return row;
        });
    }

    public void onTabSelected() {
        loadMatches();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

     public void setNewMatchButtonPressed(){

        // Opret Team Data Access Object
        TeamDAO teamDAO = new TeamDAO();
        // Opret en observable list med alle vores teams (selectAll)
        ObservableList<TeamModel> teams = FXCollections.observableArrayList(teamDAO.selectAll());

        // Opret ny Dialog og set title
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Ny kamp");

        //
        ButtonType opretBtn = new ButtonType("Start", ButtonBar.ButtonData.OK_DONE);
        //add knappen + en cancel knap
        dialog.getDialogPane().getButtonTypes().addAll(opretBtn, ButtonType.CANCEL);

        // Gem OK knappen i en Node til brug for event senere,
        // kræves for at holde vores error dialog box åben
        // Da javaFx ellers per default lukker vores Dialog fordi den auto closer efter et OK
        // event klik.... Vi vil holde vores Dialog åben hvis der er være fejl
        Node okBtn = dialog.getDialogPane().lookupButton(opretBtn);

        // Opret en Combobox (Selectlist)
        ComboBox<TeamModel> teamAlist = new ComboBox<>(teams);
        // Fyld den i bredden
        teamAlist.setMaxWidth(Double.MAX_VALUE);
        // Sæt default prompt tekst
        teamAlist.setPromptText("Vælg hold");

        ComboBox<TeamModel> teamBlist = new ComboBox<>(teams);
        teamBlist.setMaxWidth(Double.MAX_VALUE);
        teamBlist.setPromptText("Vælg hold");

        // Opret det indhold vi skal have i Dialog boksen
        VBox content = new VBox(10);
        Label labelA = new Label("Hjemmehold");
        Label labelB = new Label("Udehold");
        // Tilføj vores indhold til vores VBox
        content.getChildren().addAll(labelA, teamAlist, labelB, teamBlist);
        // Tilføj vores VBox til vores Dialog
        dialog.getDialogPane().setContent(content);

        // Event filter på vores gemte knap NODE
        // med consume i tilfælde af fejl, så vores Dialog ikke lukker ved fejl
        okBtn.addEventFilter(ActionEvent.ACTION, e -> {

             TeamModel teamA = teamAlist.getValue();
             TeamModel teamB = teamBlist.getValue();

             if(teamA == null || teamB == null) {
                 showError("Du skal vælge 2 hold");
                 e.consume();
                 return;
             }

             if(teamA.equals(teamB)) {
                 showError("Et hold kan ikke spille mod sig selv\nVælg 2 forskellige hold");
                 e.consume();
                 return;
             }

            System.out.println("Ok done");

             // If all is okay run this code
            MatchDAO matchDAO = new MatchDAO();
            MatchModel match = new MatchModel(teamA, teamB, null, null, MatchStatus.NOT_STARTED);
            matchDAO.create(match);
            mainController.focusLiveMatchTab(match);
         });
         // show our Dialog
         dialog.showAndWait();
    }

    private void loadMatches() {
        MatchDAO matchDAO = new MatchDAO();
        ObservableList<MatchModel> matches = FXCollections.observableList(matchDAO.selectAll());

        // This line is Java reflection, tries in order: nameProperty(), getName(), isName()
        // nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // this uses the getter directly
//        teamAColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        teamAColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getTeam1().getName()));
        scoreBoardColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        teamBColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getTeam2().getName()));
//        actions.setCellFactory();
        matchTable.setItems(matches);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ugyldigt valg");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
