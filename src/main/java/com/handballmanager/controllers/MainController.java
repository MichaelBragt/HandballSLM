package com.handballmanager.controllers;

import com.handballmanager.models.MatchModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class MainController {

    public AnchorPane root;
    @FXML private TabPane mainTabPane;
    @FXML private Tab liveMatchTab;
    @FXML private Tab page1Tab;
    @FXML private Tab page2Tab;
    @FXML private Tab page3Tab;

    private Page1Controller page1Controller;
    private Page2Controller page2Controller;
    private Page3Controller page3Controller;
    private MatchPageController liveMatchPageController;
    private MatchReportController matchReportController;


    /**
     * JavaFX initialize method that runds one time when controller is loaded
     * here we call our load tabs methods
     * tabPage1 is included via <fx:include source="page1.fxml" /> in the FXML file
     */
    public void initialize() {

        loadTabPage2();
        loadTabPage3();
        loadLiveMatchTab();

    }

    /**
     * Method to load tab page 2
     */
    protected void loadTabPage2() {

        // Here wer set up loading the page 2 controller
        // Parent loads the root Node on page 2, and then we set the content of this tab to page2 content
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

        // This checks if tab2 is selected and call onTabSelected when we select Tab 2
        // In page2 controller this method loads the table, so we are sure to get a fresh loaded tabel
        // when we select the tab
        page2Tab.setOnSelectionChanged(e -> {
            if(page2Tab.isSelected()) {
                page2Controller.onTabSelected();
            }
        });
    }

    /**
     * Method to set up and load our MatchReport Controller and set
     * page2Tab to it's content
     * We use this to show a match report when we doubleclick a match
     * @param match
     */
    public void loadPage2Report(MatchModel match) {
        try {
            FXMLLoader reportLoader = new FXMLLoader(
                    getClass().getResource("/com/handballmanager/matchReport.fxml")
            );
            Parent reportView = reportLoader.load();

            matchReportController = reportLoader.getController();
            matchReportController.setMatch(match);
            matchReportController.setMainController(this);

            page2Tab.setContent(reportView);

        } catch (IOException e) {
            e.printStackTrace();
        }
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
