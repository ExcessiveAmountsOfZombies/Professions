package com.epherical.professions.client.screen.editors;

import com.epherical.professions.client.screen.entry.ArrayEntry;
import com.epherical.professions.client.screen.entry.BooleanEntry;
import com.epherical.professions.client.screen.entry.DatapackEntry;
import com.epherical.professions.client.screen.entry.TagEntry;

import java.util.List;

public class DataTagEditor<T> extends DatapackEditor {

    private final BooleanEntry replace;
    private final ArrayEntry<TagEntry<T>> values;

    public DataTagEditor() {
        replace = new BooleanEntry(0, 0, 128, "Replace", false);
        values = new ArrayEntry<>(0, 0, 128, "Values");
    }

    @Override
    public List<DatapackEntry> entries() {
        return List.of(replace, values);
    }


    @Override
    public void serialize() {

    }

}
