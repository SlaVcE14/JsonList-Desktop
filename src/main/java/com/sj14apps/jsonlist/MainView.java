package com.sj14apps.jsonlist;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;

public class MainView {

    @FXML
    StackPane rawJsonRL;

    @FXML
    SplitPane mainLL;

    @FXML
    void onBack(){
        System.exit(0);
    }

    public void searchUrl(ActionEvent actionEvent) {
    }

    @FXML
    public void toggleSplitView(ActionEvent actionEvent) {
        if (rawJsonRL.isVisible()){
            rawJsonRL.setVisible(false);
            mainLL.getItems().remove(rawJsonRL);
        }else{
            rawJsonRL.setVisible(true);
            mainLL.getItems().add(rawJsonRL);

        }

    }
}
