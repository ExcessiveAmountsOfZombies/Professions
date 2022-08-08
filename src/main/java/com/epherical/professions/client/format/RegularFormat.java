package com.epherical.professions.client.format;

import com.epherical.professions.client.entry.DatapackEntry;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;

public class RegularFormat<T> implements Format<T> {

    private final TriFunction<Integer, Integer, Integer, List<DatapackEntry<T, ?>>> entries;

    public RegularFormat(TriFunction<Integer, Integer, Integer, List<DatapackEntry<T, ?>>> entries) {
        this.entries = entries;
    }

    @Override
    public TriFunction<Integer, Integer, Integer, List<DatapackEntry<T, ?>>> entries() {
        return entries;
    }
}
