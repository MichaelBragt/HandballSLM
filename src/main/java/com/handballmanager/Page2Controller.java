package com.handballmanager;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Page2Controller {



    public TableView<MatchModel> matchTable;
    public TableColumn<MatchModel, String> teamAColumn;
    public TableColumn<MatchModel, String> scoreBoardColumn;
    public TableColumn<MatchModel, String> teamBColumn;
    public Label counter;
    public VBox vBoxContent;
    Stage window = new Stage();

    public Button newMatchButton;

    public void initialize() {
        loadMatches();
    }

    public void setNewMatchButtonPressed(ActionEvent e){
         System.out.println("new match button pressed");
    }

    private void loadMatches() {
        MatchDAO matchDAO = new MatchDAO();
        ObservableList<MatchModel> matches = FXCollections.observableList(matchDAO.selectAll());

        // This line is Java reflection, tries in order: nameProperty(), getName(), isName()
        // nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // this uses the getter directly
//        teamAColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        teamAColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getTeam1().getName()));
        scoreBoardColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        teamBColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getTeam2().getName()));
//        actions.setCellFactory();
        matchTable.setItems(matches);
    }

}
