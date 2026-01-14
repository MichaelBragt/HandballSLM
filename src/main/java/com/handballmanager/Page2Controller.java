package com.handballmanager;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.lang.reflect.Array;

public class Page2Controller {

    Stage window = new Stage();

    public Button newMatchButton;

     public void setNewMatchButtonPressed(ActionEvent e){
            Array[] hold = {"hold1", "hold2", "hold3"};

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
             dialog.showAndWait().ifPresent(name -> {
                 System.out.println("Opretter hold: " + name);
                 TeamModel teamModel = new TeamModel(name);
                 teamDB.create(teamModel);
                 loadTeams();
                 // TODO: tilføj til TableView / model
             });
    }

}
