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
import com.sj14apps.jsonlist.core.controllers.WebManager;
import javafx.animation.PauseTransition;
import javafx.application.Application;

import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static com.sj14apps.jsonlist.core.JsonFunctions.getListFromPath;

public class MainApplication extends Application {

    Scene scene;
    public MainView controller;

    public JsonData data = new JsonData();

boolean isMenuOpen, isTopMenuVisible, isUrlSearching, isVertical = true;
// todo   PathListAdapter pathAdapter;
// todo   View menu, dim_bg, pathListView;
// todo   AutoTransition autoTransition = new AutoTransition();
// todo   Handler handler = new Handler();
    Thread readFileThread;
    AppState state;
    RawJsonView rawJsonView;
    FileManager fileManager;
    WebManager webController; //todo
    JsonLoader jsonLoader;
// todo   ArrayList<String> filterList = new ArrayList<>();


    @Override
    public void start(Stage stage) throws IOException {
        initialize(stage);
        LoadStateData();
        setEvents();

    }

    private void initialize(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));

        scene = new Scene(fxmlLoader.load(), stage.getWidth(), stage.getHeight());
        controller = fxmlLoader.getController();
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("images/icon.png"))));
        stage.setTitle("Json List");
        stage.setScene(scene);
        stage.show();
        stage.widthProperty().addListener((o, oldValue, newValue)->{
            if(newValue.intValue() < 700.0) {
                controller.mainLL.setOrientation(Orientation.VERTICAL);
                controller.rawJsonRL.setPadding(new Insets(0,10,10,10));
            }else {
                controller.mainLL.setOrientation(Orientation.HORIZONTAL);
                controller.rawJsonRL.setPadding(new Insets(10,10,10,0));
            }
        });

        stage.setMaximized(true);

        //todo colors
        rawJsonView = new DesktopRawJsonView(this,
                scene,
                0x414659,
                0x4A5C92,
                0x735471,
                0xBA1A1A,
                0xDDE1F9);
        rawJsonView.showJson = true;
        rawJsonView.toggleSplitView();

        //todo webController

        rawJsonView.updateRawJson("");

        fileManager = new DesktopFileManager(stage,this);
        jsonLoader = new DesktopJsonLoader(this);


        //todo DragAndDrop

    }



    private void setEvents() {

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE){
                goBack();
            }
        });


        controller.menuBtn.setOnAction(e -> fileManager.importFromFile());

        controller.openFileBtn.setOnAction(e -> fileManager.importFromFile());
        controller.backBtn.setOnAction(e -> goBack());

        controller.splitViewBtn.setOnAction(e -> rawJsonView.toggleSplitView());
    }
    public void LoadStateData() {
        boolean prevSH = state != null && state.isSyntaxHighlighting();

        state = new AppState(); //todo FileSystem.loadStateData(this);

        if (rawJsonView.isRawJsonLoaded && prevSH != state.isSyntaxHighlighting()) {
            rawJsonView.isRawJsonLoaded = false;
            if (rawJsonView.showJson)
                rawJsonView.ShowJSON();
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
        controller.titleTxt.setText(Title);
        ArrayList<ListItem> arrayList = getListFromPath(path,data.getRootList());
        data.setCurrentList(arrayList);
        controller.list.getItems().clear();
        controller.list.getItems().addAll(arrayList);
        controller.list.setCellFactory(this::ListAdapter);
        if (previousPosition == -1) {
            delay(400,() ->{
                highlightedItem = data.getPreviousPos();
                controller.list.refresh();
                controller.list.scrollTo(data.getPreviousPos()-2);
            });
        }
        else data.addPreviousPos(previousPosition);
        if (!path.isEmpty()) {
          controller.showBackBtn();
        } else controller.hideBackBtn();

    }

    public static void delay(long millis, Runnable continuation) {
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try { Thread.sleep(millis); }
                catch (InterruptedException e) { }
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> continuation.run());
        new Thread(sleeper).start();
    }

    FileManager.FileCallback fileCallback = new FileManager.FileCallback() {
        @Override
        public void onFileLoaded(String data) {
            if (data == null) {
                System.out.println("ReadFile: null data");
                return;
            }
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
                controller.list.getItems().clear();
                controller.list.getItems().addAll(data.getRootList());
                controller.list.setCellFactory(listItemListView -> ListAdapter(listItemListView));

                controller.openBtns.setVisible(false);
                controller.list.setVisible(true);
                controller.hideBackBtn();
                controller.titleTxt.setText("");
                data.clearPath();
        }

        @Override
        public void after() {
            rawJsonView.isRawJsonLoaded = false;
            if (rawJsonView.showJson){
                rawJsonView.ShowJSON();
            }
        }
    };

    private int cssColorToHex(String rgbColor) {
        Color color = Color.decode(rgbColor);
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return (r << 16) | (g << 8) | b;
    }
    public int highlightedItem = -1;
    private ListCell<ListItem> ListAdapter(ListView<ListItem> lv) {
        return new ListCell<>() {
            private AnchorPane root;
            private ListItemController listController;
            private PauseTransition resetStylePause = new PauseTransition(Duration.seconds(1));

            {
                try {
                    FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("list_item.fxml"));
                    root = loader.load();
                    listController = loader.getController();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void updateItem(ListItem item, boolean empty) {
                super.updateItem(item, empty);


                if (empty || item == null || item.isSpace()) {
                    setStyle("");
                    setGraphic(null);
                } else {

                    setStyle("");

                    listController.itemVB.getChildren().clear();

                    if (item.getName() != null) {
                        listController.itemVB.getChildren().add(listController.itemName);
                        listController.itemName.setText(item.getName());
                    }

                    if(!(item.isArray() || item.isObject())){
                        listController.itemVB.getChildren().add(listController.itemValue);
                        listController.itemValue.setText(item.getValue());
                    }

                    if (highlightedItem != -1 && highlightedItem == getIndex()){
                        setStyle("-fx-background-color: yellow;");
                        resetStylePause.setOnFinished(e -> {
                            highlightedItem = -1;
                            controller.list.refresh();
                        });
                        resetStylePause.play();
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
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit?");
            alert.setHeaderText("Do you want to exit Json List?");
            alert.getDialogPane().getButtonTypes().setAll(ButtonType.YES,ButtonType.NO);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.YES){
                System.exit(0);
            }
            return;
        }
        data.goBack();
        open(JsonData.getPathFormat(data.getPath()), data.getPath(),-1);

    }


}