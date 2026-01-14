package com.handballmanager;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.lang.reflect.Array;

public class Page2Controller {

    public TableView<MatchModel> matchTable;
    public TableColumn<MatchModel, String> teamAColumn;
    public TableColumn<MatchModel, String> scoreBoardColumn;
    public TableColumn<MatchModel, String> teamBColumn;
    public Label counter;
    public VBox vBoxContent;
    Stage window = new Stage();

    public Button newMatchButton;

    public void initialize() {
        loadMatches();
    }

     public void setNewMatchButtonPressed(ActionEvent e){
//            Array[] hold = {"hold1", "hold2", "hold3"};

             // ny Dialog og set title
             Dialog<String> dialog = new Dialog<>();
             dialog.setTitle("Ny kamp");

             // lav en opret hold knap i dialog boksen (ok knap)
             ButtonType opretBtn = new ButtonType("Start", ButtonBar.ButtonData.OK_DONE);
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
//             dialog.showAndWait().ifPresent(name -> {
//                 System.out.println("Opretter hold: " + name);
//                 TeamModel teamModel = new TeamModel(name);
//                 teamDB.create(teamModel);
//                 loadTeams();
//                 // TODO: tilføj til TableView / model
//             });
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

}
