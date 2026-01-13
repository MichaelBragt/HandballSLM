package com.handballmanager;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.awt.event.ActionEvent;

public class MainController {

    @FXML
    private Button createTeamButton;

    @FXML
    private TableView<?> matchTable;

    @FXML
    private Button newMatchButton;

    @FXML
    private TableColumn<?, ?> scoreColumn;

    @FXML
    private TableColumn<?, ?> teamNameColumn;

    @FXML
    private TableColumn<?, ?> teamNameLColumn;

    @FXML
    private TableColumn<?, ?> teamNameRColumn;

    @FXML
    private TableColumn<?, ?> teamNameVTLColumn;

    @FXML
    private TableView<?> teamTable;

    public void onCreateTeamButtonClick(ActionEvent event){
        System.out.println("add team pressed!");
    }

    public void newMatchButton(ActionEvent event){
        System.out.println("add match pressed!");
    }

}

