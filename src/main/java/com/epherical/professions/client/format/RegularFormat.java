package com.epherical.professions.client.format;

import com.epherical.professions.client.entry.DatapackEntry;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;
import java.util.function.Consumer;

public class RegularFormat<T> implements Format<T> {

    private final TriFunction<Integer, Integer, Integer, List<DatapackEntry>> entries;
    private final Consumer<T> deserializer;

    public RegularFormat(TriFunction<Integer, Integer, Integer, List<DatapackEntry>> entries, Consumer<T> deserializer) {
        this.entries = entries;
        this.deserializer = deserializer;
    }

    public RegularFormat(TriFunction<Integer, Integer, Integer, List<DatapackEntry>> entries) {
        this(entries, t -> {});
    }

    @Override
    public TriFunction<Integer, Integer, Integer, List<DatapackEntry>> entries() {
        return entries;
    }
}
