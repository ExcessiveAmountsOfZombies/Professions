package com.epherical.professions.client.screen.editors;

import com.epherical.professions.client.screen.entry.ArrayEntry;
import com.epherical.professions.client.screen.entry.BooleanEntry;
import com.epherical.professions.client.screen.entry.DatapackEntry;
import com.epherical.professions.client.screen.entry.MultipleTypeEntry;
import com.epherical.professions.client.screen.entry.TagEntry;

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
    public void serialize() {

    }

}