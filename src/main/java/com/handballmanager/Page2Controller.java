package com.handballmanager;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Page2Controller {

    Stage window = new Stage();

    public Button newMatchButton;

     public void setNewMatchButtonPressed(ActionEvent e){
         System.out.println("new match button pressed");
    }

}
