module com.sj14apps.jsonlist {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires com.google.gson;
    requires java.desktop;
    requires java.prefs;
    requires com.sj14apps.jsonlist.core;

    opens com.sj14apps.jsonlist to javafx.fxml;
    exports com.sj14apps.jsonlist;

    opens com.sj14apps.jsonlist.controllers to javafx.fxml;
}