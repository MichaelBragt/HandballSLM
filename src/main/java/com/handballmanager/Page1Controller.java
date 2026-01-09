package com.handballmanager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class Page1Controller {

    @FXML
    public Label counter;

    @FXML
    private StackPane contentBox;

    public void bindToTimer(MatchTimeManager timer) {
        timer.setListener(remaining ->
                Platform.runLater(() ->
                        counter.setText(Long.toString(remaining))
                )
        );
    }
}
