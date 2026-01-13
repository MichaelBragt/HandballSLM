module com.handballmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.handballmanager to javafx.fxml;
    exports com.handballmanager;
}