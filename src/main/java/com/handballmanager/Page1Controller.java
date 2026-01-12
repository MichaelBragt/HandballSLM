package com.handballmanager;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class Page1Controller {

    public TableView<TeamModel> teamTable;
    public TableColumn<TeamModel, String> nameColumn;
    public TableColumn<TeamModel, String> statColumn;
    TeamDAO teamDB = new TeamDAO();

    @FXML
    public Label counter;
    public VBox vBoxContent;

    @FXML
    private StackPane contentBox;

    public void initialize() {
        loadTeams();
    }

    public void bindToTimer(MatchTimeManager timer) {
        timer.setListener(remaining ->
                Platform.runLater(() ->
                        counter.setText(Long.toString(remaining))
                )
        );
    }

    /**
     * Simpel DIALOG til popup vindue til hold oprettelse
     */
    public void createTeam() {

        // ny Dialog og set title
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Opret Hold");

        // lav en opret hold knap i dialog boksen (ok knap)
        ButtonType opretBtn = new ButtonType("Opret", ButtonBar.ButtonData.OK_DONE);
        //add knappen + en cancel knap
        dialog.getDialogPane().getButtonTypes().addAll(opretBtn, ButtonType.CANCEL);

        // Lav et tesktfelt med prompt title
        TextField holdNavn = new TextField();
        holdNavn.setPromptText("Holdnavn");

        // sæt indhold af dialog boksen
        dialog.getDialogPane().setContent(
                new VBox(10, new Label("Holdnavn:"), holdNavn)
        );

        // HVIS Ok klikkes converter return værdien til en string
        dialog.setResultConverter(button -> {
            if(button == opretBtn) {
                return holdNavn.getText();
            }
            return null;
        });

        // Vis dialogboksen og check om der er en return værdi (skriv i terminal)
        dialog.showAndWait().ifPresent(name -> {
            System.out.println("Opretter hold: " + name);
            TeamModel teamModel = new TeamModel(name);
            teamDB.create(teamModel);
            loadTeams();
            // TODO: tilføj til TableView / model
        });
    }

    private void loadTeams() {
        TeamDAO teamDAO = new TeamDAO();
        ObservableList<TeamModel> teams = FXCollections.observableList(teamDAO.selectAll());

        // This line is Java reflection, tries in order: nameProperty(), getName(), isName()
        // nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // this uses the getter directly
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        statColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        teamTable.setItems(teams);
    }
}
