package com.epherical.professions.client.screen.editors;

import com.epherical.professions.client.screen.entry.ArrayEntry;
import com.epherical.professions.client.screen.entry.BooleanEntry;
import com.epherical.professions.client.screen.entry.DatapackEntry;
import com.epherical.professions.client.screen.entry.MultipleTypeEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.BuiltinRegistries;

import java.util.List;
import java.util.function.BiFunction;

public class DataTagEditor<T> extends DatapackEditor {

    private final BooleanEntry replace;
    private final ArrayEntry<MultipleTypeEntry> values;

    public DataTagEditor(BiFunction<Integer, Integer, MultipleTypeEntry> addObject) {
        replace = new BooleanEntry(0, 0, 128, "Replace", false);
        values = new ArrayEntry<>(0, 0, 128, "Values", addObject);
        //values = new ArrayEntry<>(0, 0, 128, "Values", addObject);
    }

    @Override
    public List<DatapackEntry> entries() {
        return List.of(replace, values);
    }


    @Override
    public void serialize(JsonElement object) {
        JsonObject object1 = new JsonObject();
        object1.add("replace", replace.getSerializedValue());
        object1.add("values", values.getSerializedValue());

            // todo: just do getters on everything, starting from the top, working our way down so we can serialize
            //  in a nicer way. Compound entries will return JsonObjects etc.

        System.out.println(object1);
        /*replace.serialize(object1);
        values.serialize(object1);*/

    }

}
