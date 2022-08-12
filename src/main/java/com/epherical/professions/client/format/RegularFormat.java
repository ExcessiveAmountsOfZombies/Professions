package com.epherical.professions.client.format;

import org.apache.commons.lang3.function.TriFunction;

public class RegularFormat<T> implements Format<T> {

    private final TriFunction<Integer, Integer, Integer, FormatEntryBuilder<T>> entries;

    public RegularFormat(TriFunction<Integer, Integer, Integer, FormatEntryBuilder<T>> entries) {
        this.entries = entries;
    }

    @Override
    public TriFunction<Integer, Integer, Integer, FormatEntryBuilder<T>> entries() {
        return entries;
    }
}
