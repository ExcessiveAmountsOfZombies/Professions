package com.epherical.professions.client.entry;

import com.epherical.professions.client.screen.CommonDataScreen;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.List;
import java.util.Optional;

public class CompoundEntry<OBJ> extends AbstractCompoundEntry<OBJ, CompoundEntry<OBJ>> {


    /**
     * @param entries a list of entries that will comprise the object. Each entry should use the Optional serialization key.
     */
    public CompoundEntry(int x, int y, int width, List<DatapackEntry> entries, Type... types) {
        super(x, y, width, entries, types);
        children.addAll(entries);
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
                Optional<String> serializationKey = entry.getSerializationKey();
                serializationKey.ifPresent(s -> object.add(s, entry.getSerializedValue()));
            }
        }

        return object;
    }

    @Override
    public void deserialize(OBJ object) {

    }

    @Override
    public void tick(CommonDataScreen screen) {
        super.tick(screen);
        for (AbstractWidget child : children) {
            if (child instanceof DatapackEntry entry) {
                entry.tick(screen);
            }
        }
    }

    @Override
    public void onRebuild(CommonDataScreen screen) {
        rebuildTinyButtons(screen);
        screen.addChild(this);
        for (AbstractWidget child : children) {
            if (child instanceof DatapackEntry entry) {
                entry.onRebuild(screen);
            } else {
                // compound entries shouldn't have anything in them, they're just containers.
                screen.addChild(child);
            }
        }
    }
}
