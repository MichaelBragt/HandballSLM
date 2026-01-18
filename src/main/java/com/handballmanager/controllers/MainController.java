package com.handballmanager.controllers;

import com.handballmanager.models.MatchModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;

public class MainController {

    @FXML private TabPane mainTabPane;
    @FXML private Tab liveMatchTab;
    @FXML private Tab page1Tab;
    @FXML private Tab page2Tab;
    @FXML private Tab page3Tab;

    private Page1Controller page1Controller;
    private Page2Controller page2Controller;
    private Page3Controller page3Controller;
    private MatchPageController liveMatchPageController;


    /**
     * JavaFX initialize method that runds one time when controller is loaded
     */
    public void initialize() {

        loadTabPage2();
        loadTabPage3();
        loadLiveMatchTab();

    }

    /**
     * Method to tab page 2
     */
    private void loadTabPage2() {

        try {
            FXMLLoader page2Loader = new FXMLLoader(getClass().getResource("/com/handballmanager/page2.fxml"));
            Parent page2View = page2Loader.load();
            page2Controller = page2Loader.getController();
            page2Tab.setContent(page2View);

            // We need to inject a reference to the MainController into Page2Controller to get access to method
            // focusLiveMatchTab
            page2Controller.setMainController(this);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        page2Tab.setOnSelectionChanged(e -> {
            if(page2Tab.isSelected()) {
                page2Controller.onTabSelected();
            }
        });
    }

    /**
     * Method to tab page 3
     */
    private void loadTabPage3() {

        try {
            FXMLLoader page3Loader = new FXMLLoader(getClass().getResource("/com/handballmanager/page3.fxml"));
            Parent page3View = page3Loader.load();
            page3Controller = page3Loader.getController();
            page3Tab.setContent(page3View);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to load live match page
     */
    private void loadLiveMatchTab() {

        try {
            // We create a liveMatchPageview that fetches match.fxml and gets the controller
            // and set the content of the liveMatchTab (id in main.fxml) to be this matchPage.fxml we load
            FXMLLoader matchLoader = new FXMLLoader(getClass().getResource("/com/handballmanager/matchPage.fxml"));
            Parent liveMatchPageview = matchLoader.load();
            liveMatchPageController = matchLoader.getController();
            liveMatchTab.setContent(liveMatchPageview);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * method called via Page2Controller to switch tab to Live Match
     * and pass in the just created match object
     * @param match
     */
    public void focusLiveMatchTab(MatchModel match) {
        liveMatchPageController.setMatch(match);
        mainTabPane.getSelectionModel().select(liveMatchTab);
    }
}
