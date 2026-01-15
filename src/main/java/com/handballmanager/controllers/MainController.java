package com.handballmanager.controllers;

import com.handballmanager.MatchTimeManager;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainController {

    @FXML public TabPane mainTabPane;
    @FXML private Tab liveMatchTab;
    @FXML private Tab teamsTab;
    public Tab matchesTab;

    private Parent page1View;
    private Page1Controller page1Controller;
    @FXML private Page2Controller LiveMatchViewController;

    MatchTimeManager timer = new MatchTimeManager();

    public void initialize() {
        LiveMatchViewController.setMainController(this);
        timer.startGameTimer(1);
    }

    public void focusLiveMatchTab() {
        mainTabPane.getSelectionModel().select(liveMatchTab);
    }
}
