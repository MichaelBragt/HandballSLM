package com.handballmanager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ManagerController {

    MatchTimeManager timer = new MatchTimeManager();

    @FXML
    public Label counter;

    @FXML
    private Label welcomeText;

    public void initialize() {

        timer.setListener(remaining ->
                Platform.runLater(() ->
                        counter.setText(Long.toString(remaining))
                )
        );
        timer.startGameTimer(1);

//        counter.setText("TEST");
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
