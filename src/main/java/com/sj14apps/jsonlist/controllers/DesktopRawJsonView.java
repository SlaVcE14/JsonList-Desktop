package com.sj14apps.jsonlist.controllers;

import com.sj14apps.jsonlist.MainApplication;
import com.sj14apps.jsonlist.core.JsonFunctions;
import com.sj14apps.jsonlist.core.controllers.RawJsonView;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;

public class DesktopRawJsonView extends RawJsonView {

    MainApplication main;
    Scene scene;

    public DesktopRawJsonView(MainApplication main, Scene scene, int textColor, int keyColor, int numberColor, int booleanAndNullColor, int bgColor) {
        super(textColor, keyColor, numberColor, booleanAndNullColor, bgColor);
        this.main = main;
        this.scene = scene;
    }

    @Override
    public void toggleSplitView() {

    }

    @Override
    public void ShowJSON() {
    }

    @Override
    public void updateRawJson(String string) {

        //TODO change this
        String rawJson = generateHtml(JsonFunctions.getAsPrettyPrint(string),null);
        WebEngine webEngine = main.rawJsonWV.getEngine();
        webEngine.loadContent(rawJson);
    }
}
