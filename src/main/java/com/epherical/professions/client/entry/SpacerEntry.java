package com.epherical.professions.client.entry;

import com.epherical.professions.client.screen.CommonDataScreen;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import java.util.Optional;

public class SpacerEntry extends DatapackEntry {

    public SpacerEntry(int x, int y, int width, int height, Optional optionalSerializationKey, Type... types) {
        super(x, y, width, height, optionalSerializationKey, types);
    }

    @Override
    public void onRebuild(CommonDataScreen screen) {
        screen.addChild(this);

    }

    @Override
    public JsonElement getSerializedValue() {
        return JsonNull.INSTANCE;
    }

    @Override
    public void deserialize(Object object) {

    }

    @Override
    public String getType() {
        return "Spacer";
    }
}
