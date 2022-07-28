package com.epherical.professions.client.screen.editors;

import com.epherical.professions.client.screen.entry.DatapackEntry;
import com.google.gson.JsonObject;

import java.util.List;

public abstract class DatapackEditor {

    public DatapackEditor() {

    }


    public abstract List<DatapackEntry> entries();

    public abstract void serialize(JsonObject object);
}
