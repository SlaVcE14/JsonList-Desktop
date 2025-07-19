package com.sj14apps.jsonlist;

import com.sj14apps.jsonlist.core.ListItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;


public class MainView {

    public BorderPane mainView;

    private double zoomFactor = 1.0;
    private static final double ZOOM_DELTA = 0.1;

    @FXML
    public HBox topPanel;
    @FXML
    Button backBtn;
    @FXML
    Label titleTxt;
    @FXML
    Button splitViewBtn;
    @FXML
    Button menuBtn;
    @FXML
    SplitPane mainLL;
    @FXML
    VBox urlView;
    @FXML
    StackPane listRL;
    @FXML
    VBox openBtns;
    @FXML
    ImageView fileImg;
    @FXML
    Button openFileBtn;
    @FXML
    Button openUrlBtn;
    @FXML
    ListView<ListItem> list;
    @FXML
    StackPane rawJsonRL;
    @FXML
    public WebView rawJsonWV;
    @FXML
    Rectangle fullRawBtn;
    @FXML
    TextField urlSearch;
    @FXML
    Button searchUrlBtn;


    // todo filterBtn;
    // todo   EditText urlSearch;
    // todo   LinearLayout urlLL;
    // todo   RecyclerView pathList;
    // todo   LinearLayout progressView;
    // todo   LinearProgressIndicator progressBar;

    // todo   LinearLayout topMenu;

    public void searchUrl(ActionEvent actionEvent) {
    }

    @FXML
    public void toggleSplitView() {
        if (rawJsonRL.isVisible()){
            rawJsonRL.setVisible(false);
            mainLL.getItems().remove(rawJsonRL);
        }else{
            rawJsonRL.setVisible(true);
            mainLL.getItems().add(rawJsonRL);
        }

    }

    public void hideBackBtn() {
        topPanel.getChildren().remove(backBtn);
    }

    public void showBackBtn() {
        if (!topPanel.getChildren().contains(backBtn))
            topPanel.getChildren().addFirst(backBtn);
    }

    @FXML
    private void handleZoom(ScrollEvent event) {
        if (event.isControlDown()) {
            if (event.getDeltaY() > 0) {
                zoomFactor += ZOOM_DELTA;
            } else {
                zoomFactor = Math.max(0.5, zoomFactor - ZOOM_DELTA);
            }
            rawJsonWV.setZoom(zoomFactor);
            event.consume();
        }
    }

    public void hideUrlSearchView() {
        urlView.setVisible(false);
        mainLL.setVisible(true);
        urlSearch.clear();

    }

    public void showLinkView() {
        mainLL.setVisible(false);
        urlView.setVisible(true);
    }
}
