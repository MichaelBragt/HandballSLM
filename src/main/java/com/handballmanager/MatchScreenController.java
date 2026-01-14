package com.handballmanager;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MatchScreenController {
    public Label counter;
    public Button t1GoalButton;
    public Button t1RedCardButton;
    
    public Button t2GoalButton;
    public Button t2RedCardButton;
    
    public Button startStopTimer;
    public Label scoreCounterT1;
    public Label scoreCounterT2;

    private MatchTimeManager timer = new MatchTimeManager();



    public void initialize() {
        timer.startGameTimer(1);
        timer.setListener(remaining ->
                Platform.runLater(() ->
                        counter.setText(Long.toString(remaining))
                )
        );
        }

}
