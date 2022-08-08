package com.epherical.professions.client.entry;

import com.epherical.professions.client.screen.CommonDataScreen;
import com.google.gson.JsonElement;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.List;
import java.util.Optional;

public class CompoundEntry<OBJ> extends AbstractCompoundEntry<OBJ, CompoundEntry<OBJ>> {


    private final Deserializer<OBJ, CompoundEntry<OBJ>> deserializer;

    /**
     * @param entries a list of entries that will comprise the object. Each entry should use the Optional serialization key.
     */
    public CompoundEntry(int x, int y, int width, Optional<String> key, List<DatapackEntry<OBJ, ?>> entries, Deserializer<OBJ, CompoundEntry<OBJ>> deserializer, Type... types) {
        super(x, y, width, key, entries, types);
        this.deserializer = deserializer;
    }

    @Override
    public String getType() {
        return "Compound";
    }

    @Override
    public JsonElement getSerializedValue() {
        return super.getSerializedValue();
    }

    @Override
    public void deserialize(OBJ object) {
        deserializer.deserialize(object, this);
    }

    @Override
    public void tick(CommonDataScreen screen) {
        super.tick(screen);
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
