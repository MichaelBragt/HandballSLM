package com.handballmanager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.util.Objects;


public class ManagerController {

    private Parent page1View;
    private Page1Controller page1Controller;

    MatchTimeManager timer = new MatchTimeManager();

    @FXML
    private StackPane contentBox;

    public void initialize() {
        timer.startGameTimer(1);

        startPage();

    }

    @FXML
    public void onPage1Click() throws IOException {
        startPage();
    }

    @FXML
    public void onPage2Click() throws IOException {
        Parent page = FXMLLoader.load(
                Objects.requireNonNull(
                    HandballApp.class.getResource("page2.fxml"),
                    "Page not found")
        );
        contentBox.getChildren().setAll(page);
    }

    /**
     * method to call startpage (page1.fxml)
     * so we can reuse it on button click AND in initialize method
     */
    private void startPage() {
        Platform.runLater(() -> {
            try {
                // We have bound the timer to the page1 controller, so if page1View is already loaded
                // no need to load it again
                if(page1View == null) {
                    // set the FXML loader to load page1
                    FXMLLoader loader = new FXMLLoader(
                            Objects.requireNonNull(
                                    HandballApp.class.getResource("page1.fxml"),
                                    "Page not found")
                    );
                    // do the actual loading
                    page1View = loader.load();
                    // get the controller
                    page1Controller = loader.getController();
                    // call the bindToTimer method with our actual timer instance
                    page1Controller.bindToTimer(timer);
                }
                contentBox.getChildren().setAll(page1View);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
