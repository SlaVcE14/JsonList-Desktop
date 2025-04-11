package com.sj14apps.jsonlist;

import com.sj14apps.jsonlist.controllers.DesktopFileManager;
import com.sj14apps.jsonlist.controllers.DesktopJsonLoader;
import com.sj14apps.jsonlist.controllers.DesktopRawJsonView;
import com.sj14apps.jsonlist.core.AppState;
import com.sj14apps.jsonlist.core.JsonData;
import com.sj14apps.jsonlist.core.ListItem;
import com.sj14apps.jsonlist.core.controllers.FileManager;
import com.sj14apps.jsonlist.core.controllers.JsonLoader;
import com.sj14apps.jsonlist.core.controllers.RawJsonView;
import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;


import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

import static com.sj14apps.jsonlist.core.JsonFunctions.getListFromPath;

public class MainApplication extends Application {

    Scene scene;

    private double zoomFactor = 1.0;
    private static final double ZOOM_DELTA = 0.1;
    Label titleTxt;
    ListView<ListItem> list;
    public JsonData data = new JsonData();
   boolean isMenuOpen, isRawJsonLoaded, isTopMenuVisible, isUrlSearching, isVertical = true;
    public WebView rawJsonWV;
    AppState state;
    RawJsonView rawJsonView;
    FileManager fileManager;
    JsonLoader jsonLoader;
    VBox openBtns;
    MainView mainController;

    @Override
    public void start(Stage stage) throws IOException {
        initialize(stage);
        setEvents();

    }

    private void initialize(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        scene = new Scene(fxmlLoader.load(), stage.getWidth(), stage.getHeight());
        mainController = fxmlLoader.getController();
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("images/icon.png"))));
        stage.setTitle("Json List");
        stage.setScene(scene);
        stage.show();
        stage.setMaximized(true);
        rawJsonWV = (WebView) scene.lookup("#rawJsonWV");
        list = (ListView<ListItem>) scene.lookup("#list");
        openBtns = (VBox) scene.lookup("#openBtns");
        titleTxt = mainController.titleTxt;
        rawJsonView = new DesktopRawJsonView(this,
                scene,
                0x414659,
                0x4A5C92,
                0x735471,
                0xBA1A1A,
                0xDDE1F9);

        rawJsonView.updateRawJson("");

        fileManager = new DesktopFileManager(stage,this);
        jsonLoader = new DesktopJsonLoader(this);


    }
    private void handleZoom(ScrollEvent event, WebView webView) {
        if (event.isControlDown()) {
            if (event.getDeltaY() > 0) {
                zoomFactor += ZOOM_DELTA;
            } else {
                zoomFactor = Math.max(0.5, zoomFactor - ZOOM_DELTA);
            }
            webView.setZoom(zoomFactor);
            event.consume();
        }
    }

    private void setEvents() {

        rawJsonWV.setOnScroll(event -> handleZoom(event, rawJsonWV));

        ((Button) scene.lookup("#openFileBtn")).setOnAction(e -> fileManager.importFromFile());
        ((Button) scene.lookup("#backBtn")).setOnAction(e -> goBack());

    }
    public void LoadStateData() {
        boolean prevSH = state != null && state.isSyntaxHighlighting();
        if (isRawJsonLoaded && prevSH != state.isSyntaxHighlighting()) {
            isRawJsonLoaded = false;
        }
    }

    public void loadFileIntoWebView(File file) {
        try {
            fileManager.readFile(new FileInputStream(file),0,fileCallback);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.setProperty("prism.order", "d3d"); // For Windows (Direct3D)
        System.setProperty("prism.order", "es2"); // For OpenGL (Linux/macOS)
        launch();
    }

    public void open(String Title, String path, int previousPosition) {
        data.setPath(path);
        titleTxt.setText(Title);
        ArrayList<ListItem> arrayList = getListFromPath(path,data.getRootList());
        data.setCurrentList(arrayList);
        list.getItems().clear();
        list.getItems().addAll(arrayList);
        list.setCellFactory(this::ListAdapter);
        if (previousPosition == -1) {
        }
        else data.addPreviousPos(previousPosition);
    }

    FileManager.FileCallback fileCallback = new FileManager.FileCallback() {
        @Override
        public void onFileLoaded(String data) {
            rawJsonView.updateRawJson(data);
            jsonLoader.LoadData(data,jsonLoaderCallback); //todo thread??
        }

        @Override
        public void onFileLoadFailed() {

        }

        @Override
        public void onProgressUpdate(int progress) {

        }
    };

    JsonLoader.JsonLoaderCallback jsonLoaderCallback = new JsonLoader.JsonLoaderCallback() {
        @Override
        public void start() {
        }
        @Override
        public void started() {
        }
        @Override
        public void failed() {
        }
        @Override
        public void success() {
                data.setCurrentList(data.getRootList());
                list.getItems().addAll(data.getRootList());
                list.setCellFactory(listItemListView -> ListAdapter(listItemListView));
                openBtns.setVisible(false);
                list.setVisible(true);
                titleTxt.setText("");
        }
        public void after() {
        }
    };

    private int cssColorToHex(String rgbColor) {
        Color color = Color.decode(rgbColor);
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return (r << 16) | (g << 8) | b;
    }
    private ListCell<ListItem> ListAdapter(ListView<ListItem> lv) {
        return new ListCell<>() {
            private AnchorPane root;
            private ListItemController controller;

            {
                try {
                    FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("list_item.fxml"));
                    root = loader.load();
                    controller = loader.getController();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void updateItem(ListItem item, boolean empty) {
                super.updateItem(item, empty);


                if (empty || item == null || item.isSpace()) {
                    setGraphic(null);
                } else {

                    controller.itemVB.getChildren().clear();

                    if (item.getName() != null) {
                        controller.itemVB.getChildren().add(controller.itemName);
                        controller.itemName.setText(item.getName());
                    }

                    if(!(item.isArray() || item.isObject())){
                        controller.itemVB.getChildren().add(controller.itemValue);
                        controller.itemValue.setText(item.getValue());
                    }


                    setGraphic(root);
                }

            }

            @Override
            public void updateSelected(boolean b) {
                ListItem item = getItem();
                if (item.isObject() || item.isArray()) {

                    String newPath = data.getPath() + (data.getPath().equals("") ? "": "///" + (item.getId()!=-1?"{" + item.getId() + "}":"")) + item.getName();
                    open(JsonData.getPathFormat(newPath),newPath,item.getPosition()!=-1?item.getPosition():getIndex());

                }
            }
        };
    }

    public void goBack(){
        if (data.isEmptyPath()){
            System.exit(0);
            return;
        }
        data.goBack();
        open(JsonData.getPathFormat(data.getPath()), data.getPath(),-1);

    }


}