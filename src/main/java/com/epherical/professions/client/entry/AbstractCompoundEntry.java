package com.epherical.professions.client.entry;

import com.epherical.professions.client.screen.CommonDataScreen;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.List;
import java.util.Optional;

public abstract class AbstractCompoundEntry<OBJ, V extends AbstractCompoundEntry<?, ?>> extends DatapackEntry<OBJ, V> {


    private final List<DatapackEntry<OBJ, ?>> entries;

    public AbstractCompoundEntry(int x, int y, int width, Optional<String> serializationKey, List<DatapackEntry<OBJ, ?>> entries, Type... types) {
        super(x, y, width, 0, serializationKey, types);
        children.addAll(entries);
        this.entries = entries;
    }

    /**
     * @param entries a list of entries that will comprise the object. Each entry should use the Optional serialization key.
     */
    public AbstractCompoundEntry(int x, int y, int width, List<DatapackEntry<OBJ, ?>> entries, Type... types) {
        this(x, y, width, Optional.empty(), entries, types);
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
                serializationKey.ifPresent(s -> {
                    if (!entry.getSerializedValue().isJsonNull()) {
                        object.add(s, entry.getSerializedValue());
                    }
                });
            }
        }
        return object;
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

    public List<DatapackEntry<OBJ, ?>> getEntries() {
        return entries;
    }
}
