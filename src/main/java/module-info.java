module com.handballmanager {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.handballmanager to javafx.fxml;
    exports com.handballmanager;
}