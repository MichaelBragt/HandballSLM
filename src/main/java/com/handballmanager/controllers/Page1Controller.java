package com.handballmanager.controllers;

import com.handballmanager.MatchTimeManager;
import com.handballmanager.dataAccesObjects.TeamDAO;
import com.handballmanager.models.TeamModel;
import com.handballmanager.services.CreateTeamWithLeagueService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class Page1Controller {

    public TableView<TeamModel> teamTable;
    public TableColumn<TeamModel, String> nameColumn;
    public TableColumn<TeamModel, String> statColumn;
    public TableColumn<TeamModel, Void> actions;

    TeamDAO teamDB = new TeamDAO();

    @FXML
    public Label counter;
    public VBox vBoxContent;

    @FXML
    private StackPane contentBox;

    public void initialize() {
        teamTable.setEditable(true);
        nameColumn.setEditable(true);
        loadTeams();

        actions.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("🗑");
            {
                setAlignment(Pos.CENTER);
                deleteBtn.setOnAction(e -> {
                    // This line gets back the exact TeamModel instance that is in the specific row
                    // Because a TableView is a direct representation of the observable list being used
                    // to populate it so we can do this getTableView().getItems().get(getIndex());
                    // to get back the exact object and therefore be sure which object (row) we work on
                    TeamModel team = getTableView().getItems().get(getIndex());
                    // we try call delet and if succes full, we also remove it from TableView in UI
                    if(teamDB.delete(team)) {
                        getTableView().getItems().remove(team);
                    }
                });
                deleteBtn.getStyleClass().add("icon-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
        actions.setMinWidth(40);
        actions.setMaxWidth(40);
        actions.setPrefWidth(40);
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
            CreateTeamWithLeagueService createTeam = new CreateTeamWithLeagueService();
            createTeam.createTeamWithLeague(name);
//            System.out.println("Opretter hold: " + name);
//            TeamModel teamModel = new TeamModel(name);
//            teamDB.create(teamModel);
            loadTeams();
        });
    }


    private void loadTeams() {

        ObservableList<TeamModel> teams = FXCollections.observableList(teamDB.selectAll());

        // This line is Java reflection, tries in order: nameProperty(), getName(), isName()
        // nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // this uses the getter directly
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        // we use JavaFx build in functionality to edit the value in a cell so we can handle changing a teams name
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        // event to handle what to do on change commit
        nameColumn.setOnEditCommit(e -> {
            // we wrap it in a try/catch so nothing changes if we have errors
            try {
                TeamModel team = e.getRowValue(); // get and insert into model
                team.setName(e.getNewValue()); // set the new value
                teamDB.update(team); // call the database update
            }
            catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        });

        statColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        teamTable.setItems(teams);
    }
}
