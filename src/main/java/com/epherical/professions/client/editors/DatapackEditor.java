package com.epherical.professions.client.editors;

import com.epherical.professions.client.entry.DatapackEntry;
import com.google.gson.JsonObject;

import java.util.List;

public abstract class DatapackEditor {


    protected int width;

    public DatapackEditor() {

    }


    public void setWidth(int width) {
        this.width = width;
    }

    public abstract List<DatapackEntry> entries();

    public abstract void serialize(JsonObject object);
}
