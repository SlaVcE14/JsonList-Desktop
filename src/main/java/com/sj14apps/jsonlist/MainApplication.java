package com.sj14apps.jsonlist;

import com.sj14apps.jsonlist.controllers.DesktopFileManager;
import com.sj14apps.jsonlist.controllers.DesktopRawJsonView;
import com.sj14apps.jsonlist.core.controllers.FileManager;
import com.sj14apps.jsonlist.core.controllers.RawJsonView;
import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.util.Objects;

public class MainApplication extends Application {

    Scene scene;

    private double zoomFactor = 1.0;
    private static final double ZOOM_DELTA = 0.1;
   boolean isMenuOpen, isRawJsonLoaded, isTopMenuVisible, isUrlSearching, isVertical = true;
    public WebView rawJsonWV;
    RawJsonView rawJsonView;
    FileManager fileManager;

    @Override
    public void start(Stage stage) throws IOException {
        initialize(stage);
        setEvents();

    }

    private void initialize(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        scene = new Scene(fxmlLoader.load(), stage.getWidth(), stage.getHeight());
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("images/icon.png"))));
        stage.setTitle("Json List");
        stage.setScene(scene);
        stage.show();
        stage.setMaximized(true);
        rawJsonWV = (WebView) scene.lookup("#rawJsonWV");
        rawJsonView = new DesktopRawJsonView(this,
                scene,
                0x414659,
                0x4A5C92,
                0x735471,
                0xBA1A1A,
                0xDDE1F9);

        rawJsonView.updateRawJson("");

        fileManager = new DesktopFileManager(stage,this);


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

    FileManager.FileCallback fileCallback = new FileManager.FileCallback() {
        @Override
        public void onFileLoaded(String data) {
            rawJsonView.updateRawJson(data);
        }

        @Override
        public void onFileLoadFailed() {

        }

        @Override
        public void onProgressUpdate(int progress) {

        }
    };

    private int cssColorToHex(String rgbColor) {
        Color color = Color.decode(rgbColor);
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return (r << 16) | (g << 8) | b;
    }



}