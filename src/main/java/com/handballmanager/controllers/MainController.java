package com.handballmanager.controllers;

import com.handballmanager.MatchTimeManager;
import com.handballmanager.models.MatchModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;

public class MainController {

    @FXML public TabPane mainTabPane;
    @FXML private Tab liveMatchTab;
    @FXML private Tab teamsTab;
    public Tab matchesTab;

    private Parent page1View;
    private Page1Controller page1Controller;
    private MatchPageController matchPageController;

    // We need to inject the MainController into Page2Controller to get access to method
    // focusLiveMatchTab, so this naming comes from
    // <fx:include source="page2.fxml" fx:id="LiveMatchView" /> in main.fxml
    // hard rule in Java, when you include an fxml file Controller gets added to the fx:id
    // so LiveMatchView becomes LiveMatchViewController
    // that way we can do LiveMatchViewController.setMainController(this)
    // so Page2Controller has an instance of MainController
    @FXML private Page2Controller LiveMatchViewController;


    public void initialize() throws IOException {
        // We pass MainController to Page2Controller
        LiveMatchViewController.setMainController(this);

        // We create a view that fetches match.fxml and gets the controller
        // and set the content of the liveMatchTab (id in main.fxml) to be this matchPage.fxml we load
        FXMLLoader matchLoader = new FXMLLoader(getClass().getResource("/com/handballmanager/matchPage.fxml"));
        Parent view = matchLoader.load();
        matchPageController = matchLoader.getController();
        liveMatchTab.setContent(view);
    }

    /**
     * method called via Page2Controller to switch tab to Live Match
     * and pass in the just created match object
     * @param match
     */
    public void focusLiveMatchTab(MatchModel match) {
        matchPageController.setMatch(match);
        mainTabPane.getSelectionModel().select(liveMatchTab);
    }
}
