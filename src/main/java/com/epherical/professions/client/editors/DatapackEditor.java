package com.epherical.professions.client.editors;

import com.epherical.professions.client.entry.DatapackEntry;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.components.Button;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public abstract class DatapackEditor<T> {

    @Nullable
    protected T objectToBeDeserialized;

    protected int width;

    public DatapackEditor() {}

    public void setWidth(int width) {
        this.width = width;
    }

    public abstract List<DatapackEntry<T, ?>> entries();

    public abstract Collection<T> deserializableObjects();

    public abstract Collection<Button> deserializableObjectButtons(DatapackEditor<T> editor);

    public abstract void serialize(JsonObject object);

    public abstract void deserialize(T object);

    public void deserialize() {
        if (objectToBeDeserialized != null) {
            deserialize(objectToBeDeserialized);
        }
    }

    public void setObjectToBeDeserialized(@Nullable T objectToBeDeserialized) {
        this.objectToBeDeserialized = objectToBeDeserialized;
    }
}
