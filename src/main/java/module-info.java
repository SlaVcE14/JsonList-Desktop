module com.sj14apps.jsonlist {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires com.google.gson;
    requires java.desktop;


    opens com.sj14apps.jsonlist to javafx.fxml;
    exports com.sj14apps.jsonlist;
    exports com.sj14apps.jsonlist.controllers;
    opens com.sj14apps.jsonlist.controllers to javafx.fxml;
}