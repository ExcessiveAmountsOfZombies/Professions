package com.epherical.professions.client.editors;

import com.epherical.professions.client.entry.DatapackEntry;
import com.epherical.professions.client.entry.MultipleTypeEntry;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.components.Button;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Collection;
import java.util.List;

public class DataTagEditor<T> extends DatapackEditor<T> {

    /*private final BooleanEntry replace;
    private final ArrayEntry<T, MultipleTypeEntry<T>> values;*/

    public DataTagEditor(TriFunction<Integer, Integer, Integer, MultipleTypeEntry<T>> addObject) {
        /*replace = new BooleanEntry(0, 0, 128, "Replace", false);
        values = new ArrayEntry<>(0, 0, 128, "Values", addObject);*/
    }

    @Override
    public List<DatapackEntry<T, ?>> entries() {
        return null;
    }

    @Override
    public List<T> deserializableObjects() {
        return null;
    }

    @Override
    public Collection<Button> deserializableObjectButtons(DatapackEditor<T> editor) {
        return null;
    }

    @Override
    public void serialize(JsonObject object) {
        JsonObject object1 = new JsonObject();
        /*object1.add("replace", replace.getSerializedValue());
        object1.add("values", values.getSerializedValue());*/

        System.out.println(object1);
        /*replace.serialize(object1);
        values.serialize(object1);*/

    }

    @Override
    public void deserialize(Object object) {

    }

}
