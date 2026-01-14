package com.handballmanager;

import javafx.scene.control.*;

import java.time.LocalDateTime;

public class MatchPageController {
    public Button newMatchButton;
    public TableView<MatchModel> liveMatchTable;
    public TableColumn<MatchModel, String> eventDetail;
    public TableColumn<MatchModel, LocalDateTime> eventTime;
    public TableColumn<MatchModel, String> eventTeam;
    public TableColumn<MatchModel, Void> actions;
    public Label counter;

    public void initialize() {

        liveMatchTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );

        eventDetail.setResizable(true);
        eventTime.setResizable(true);
        eventTeam.setResizable(true);
        actions.setResizable(false);

//        eventDetail.setPrefWidth(1);
//        eventTime.setPrefWidth(1);
//        eventTeam.setPrefWidth(1);

        actions.setMinWidth(40);
        actions.setMaxWidth(40);
        actions.setPrefWidth(40);

        actions.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : "test");
            }
        });
    }
}
