package com.handballmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HandballApp extends Application {

//    MatchTimeManager timer = new MatchTimeManager();

    @Override
    public void start(Stage stage) throws IOException {

//        timer.startGameTimer(1);



        FXMLLoader fxmlLoader = new FXMLLoader(HandballApp.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Håndbold Kamp Manager");
        stage.setScene(scene);
        stage.show();
//        TeamDAO test = new TeamDAO();
//        List<TeamModel> teams = test.selectAll();
//        System.out.println(teams);

    }

}
