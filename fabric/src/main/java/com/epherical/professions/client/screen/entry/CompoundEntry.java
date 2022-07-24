package com.epherical.professions.client.screen.entry;

import com.epherical.professions.client.screen.DatapackScreen;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.List;

public class CompoundEntry extends DatapackEntry {


    /**
     * @param entries a list of entries that will comprise the object. Each entry should use the Optional serialization key.
     */
    public CompoundEntry(int i, int j, int k, List<DatapackEntry> entries) {
        super(i, j, k, 0);
        children.addAll(entries);

        // also need to start working on adding a scroll bar
    }

    @Override
    public String getType() {
        return "Compound";
    }

    @Override
    public JsonElement getSerializedValue() {
        JsonObject object = new JsonObject();
        for (AbstractWidget child : children) {
            if (child instanceof DatapackEntry entry) {
                object.add(entry.getSerializationKey().get(), entry.getSerializedValue());
            }
        }

        return object;
    }

    @Override
    public void tick(DatapackScreen screen) {
        super.tick(screen);
        for (AbstractWidget child : children) {
            if (child instanceof DatapackEntry entry) {
                entry.tick(screen);
            }
        }
    }
}
