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
        main.controller.toggleSplitView();
        showJson = !showJson;
        if (showJson && !isRawJsonLoaded)
            ShowJSON();
    }

    @Override
    public void ShowJSON() {

        if (main.data.getRawData().equals("-1")){
            if (showJson)
                toggleSplitView();

            return;
        }

        if (main.data.getRawData().equals(""))
            return;

        //todo thread ???
        String dataStr = JsonFunctions.getAsPrettyPrint(main.data.getRawData());
        updateRawJson(dataStr);
        isRawJsonLoaded = true;
    }

    @Override
    public void updateRawJson(String string) {

        //TODO change this
        String rawJson = generateHtml(string,null);
        WebEngine webEngine = main.controller.rawJsonWV.getEngine();
        webEngine.loadContent(rawJson);
    }
}
