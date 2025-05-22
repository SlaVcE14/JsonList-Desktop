package com.sj14apps.jsonlist.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sj14apps.jsonlist.MainApplication;
import com.sj14apps.jsonlist.core.JsonData;
import com.sj14apps.jsonlist.core.ListItem;
import com.sj14apps.jsonlist.core.controllers.JsonLoader;

import java.util.ArrayList;

import static com.sj14apps.jsonlist.core.JsonFunctions.getJsonArrayRoot;
import static com.sj14apps.jsonlist.core.JsonFunctions.getJsonObject;

public class DesktopJsonLoader implements JsonLoader {

    MainApplication application;

    public DesktopJsonLoader(MainApplication application){
        this.application = application;
    }

    @Override
    public void LoadData(String Data, String fileName, JsonLoaderCallback callBack) {
        JsonData data = application.data;

        //todo run in thread
        ArrayList<ListItem> temp = data.getRootList();
        String tempRaw = data.getRawData();
        JsonElement element;
        try {
            element = JsonParser.parseString(Data);

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            fileTooLargeException();
            return;
        } catch (Exception e){
            e.printStackTrace();
            fileNotLoadedException();
            return;
        }
        if (element == null) {
            fileNotLoadedException();
            return;
        }
        callBack.started();
        try {
            data.setRootList(null);
            if (element instanceof JsonObject) {
                JsonObject object = element.getAsJsonObject();
                data.setRootList(getJsonObject(object));
            }
            if (element instanceof JsonArray) {
                JsonArray array = element.getAsJsonArray();
                data.setRootList(getJsonArrayRoot(array));
            }
            if (Data.length()<500000)
                data.setRawData(Data);
            else data.setRawData("-1");
            data.clearPreviousPos();
        } catch (Exception e){
            e.printStackTrace();
            creatingListException();
            data.setRootList(null);
        }
        if (!data.isRootListNull()) {
            callBack.success();

        } else {
            data.setRootList(temp);
            data.setRawData(tempRaw);
            callBack.failed();
            fileNotLoadedException();
            return;
        }

        callBack.after();

    }

    void fileTooLargeException(){
     //todo   postMessageException(activity.getString(R.string.file_is_too_large));
    }
    void fileNotLoadedException(){
      //todo  postMessageException(activity.getString(R.string.fail_to_load_file));
    }
    void creatingListException(){
     //todo   postMessageException(activity.getString(R.string.fail_to_create_list));
    }
}
