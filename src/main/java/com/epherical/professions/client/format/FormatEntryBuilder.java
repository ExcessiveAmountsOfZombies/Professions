package com.epherical.professions.client.format;

import com.epherical.professions.client.entry.DatapackEntry;

import java.util.ArrayList;
import java.util.List;

public class FormatEntryBuilder<T> {

    private List<DatapackEntry<T, ?>> entries;

    public FormatEntryBuilder() {
        entries = new ArrayList<>();

    }

    public FormatEntryBuilder<T> addEntry(DatapackEntry<T, ?> entry) {
        entries.add(entry);
        return this;
    }

    public FormatEntryBuilder<T> addEntry(List<DatapackEntry<T, ?>> entries) {
        this.entries.addAll(entries);
        return this;
    }


    public List<DatapackEntry<T, ?>> build() {
        return entries;
    }

}
