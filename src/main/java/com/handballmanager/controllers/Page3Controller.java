package com.handballmanager.controllers;

import com.handballmanager.dataAccesObjects.LeagueDAO;
import com.handballmanager.dataAccesObjects.MatchDAO;
import com.handballmanager.models.LeagueModel;
import com.handballmanager.models.LeagueTableViewModel;
import com.handballmanager.models.MatchModel;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class Page3Controller {

    @FXML public TableView<LeagueTableViewModel> leagueTable;
    @FXML public TableColumn<LeagueTableViewModel, Number> rankingCol;
    @FXML public TableColumn<LeagueTableViewModel, Number> pointsCol;
    @FXML public TableColumn<LeagueTableViewModel, String> teamNameCol;
    @FXML public TableColumn<LeagueTableViewModel, Number> scoredGoalsCol;
    @FXML public TableColumn<LeagueTableViewModel, Number> goalsTakenCol;
    @FXML public TableColumn<LeagueTableViewModel, Void> matchStatsCol;

    private LeagueModel leagueModel;
    private final LeagueDAO leagueDAO = new LeagueDAO();

    /**
     * JavaFx initialize method
     */
    public void initialize() {

        loadLeague();

    }

    /**
     * method load load and insert all league data into our tableview
     */
    private void loadLeague() {

        ObservableList<LeagueTableViewModel> leagueTableList = FXCollections.observableList(leagueDAO.getLeague());

        rankingCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPosition()));
        pointsCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPoints()));
        teamNameCol.setCellValueFactory(data ->  new SimpleStringProperty(data.getValue().getTeamName()));
        scoredGoalsCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getGoalsScored()));
        goalsTakenCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getGoalsAgainst()));

        // this column needs to have several values so we create our own cell factory
        // and Override updateItem and get our wins, losses, draws from the league object on that
        // specific row
        matchStatsCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if(empty) {
                    setText(null);
                }
                else {
                    // here we get our league object from the row we are on
                    LeagueTableViewModel row = getTableRow().getItem();
                    // and set the format of our output
                    setText(row.getWins() + " - " + row.getLosses() + " - " + row.getDraws());
                }
            }
        });

        // we add it to our tableview
        leagueTable.setItems(leagueTableList);
    }
}
