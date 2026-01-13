package com.handballmanager;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ChooseTeamController implements Initializable {

    public ChoiceBox<String> teamOneDropdown;
    public ChoiceBox<String> teamTwoDropdown;
    public Button cancelButton;
    public Button okayButton;

    private String[] teams = {"team1", "team2", "team3"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        teamOneDropdown.getItems().addAll(teams);
    }
}
