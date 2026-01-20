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
    public TableColumn<TeamModel, Void> actions;
    @FXML public VBox vBoxContent;

    TeamDAO teamDB = new TeamDAO();

    /**
     * JavaFX initialize
     * Here we set up some stuff
     * we set the name column and the table to be editable so we can change team name directly in the cell
     * we also create our own table cell for our delete button and load our teams into the table with
     * our loadTeams() method call
     */
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
     * method to create our popup Dialog to create teams
     */
    public void createTeam() {

        // new Dialog and set title
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Opret Hold");

        // create a "create" button
        ButtonType opretBtn = new ButtonType("Opret", ButtonBar.ButtonData.OK_DONE);
        // add the button and a cancel button to the Dialog
        dialog.getDialogPane().getButtonTypes().addAll(opretBtn, ButtonType.CANCEL);

        // create a text field and a prompt text
        TextField holdNavn = new TextField();
        holdNavn.setPromptText("Holdnavn");

        // add it to the Dialog
        dialog.getDialogPane().setContent(
                new VBox(10, new Label("Holdnavn:"), holdNavn)
        );

        // IF OK is pressed convert the return value to a string
        // else return zero
        dialog.setResultConverter(button -> {
            if(button == opretBtn) {
                return holdNavn.getText();
            }
            return null;
        });

        // This is the actual call to show the Dialog window, we show and wait
        // and check if name (if there is a return value) is set
        // and if so we call the create team service
        dialog.showAndWait().ifPresent(name -> {
            CreateTeamWithLeagueService createTeam = new CreateTeamWithLeagueService();
            createTeam.createTeamWithLeague(name);
            loadTeams();
        });
    }

    /**
     * method to load our teams and apply them to our tableview
     */
    private void loadTeams() {

        ObservableList<TeamModel> teams = FXCollections.observableList(teamDB.selectAll());

        // This line is Java reflection, tries in order: nameProperty(), getName(), isName()
        // nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        // We decided to go with the direct getter structure

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

        // we add the teams to our teamTable tableView
        teamTable.setItems(teams);
    }
}
