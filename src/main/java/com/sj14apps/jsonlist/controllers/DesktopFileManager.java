package com.sj14apps.jsonlist.controllers;

import com.sj14apps.jsonlist.MainApplication;
import com.sj14apps.jsonlist.core.controllers.FileManager;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.prefs.Preferences;

public class DesktopFileManager implements FileManager {
    private static final String LAST_OPENED_DIR = "lastOpenedDirectory";
    Stage stage;
    MainApplication main;

    public DesktopFileManager(Stage stage, MainApplication main){
        this.stage = stage;
        this.main = main;
    }


    @Override
    public void importFromFile() {
        Preferences prefs = Preferences.userNodeForPackage(DesktopFileManager.class);

        FileChooser fileChooser = new FileChooser();
        String lastDir = prefs.get(LAST_OPENED_DIR, null);

        if (lastDir != null) {
            File lastDirectory = new File(lastDir);
            if (lastDirectory.exists()) {
                fileChooser.setInitialDirectory(lastDirectory);
            }
        }

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files", "*"));

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            prefs.put(LAST_OPENED_DIR, selectedFile.getParent());
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            main.loadFileIntoWebView(selectedFile);
        } else {
            System.out.println("No file selected.");
        }
    }

    @Override
    public void readFile(InputStream inputStream, long fileSize, FileCallback callback) {

        try {
            byte[] buffer = new byte[4096];
            StringBuilder data = new StringBuilder();
            int bytesRead;
            long totalBytesRead = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                totalBytesRead += bytesRead;
                data.append(new String(buffer, 0, bytesRead));

                if (fileSize > 0) {
                    int progress = (int) ((totalBytesRead * 100) / fileSize);
                    callback.onProgressUpdate(progress);
                }
            }
            callback.onFileLoaded(data.toString());

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
