package com.epherical.professions.client.screen.format;

import com.epherical.professions.client.screen.entry.DatapackEntry;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;

public class RegularFormat implements Format {

    private final TriFunction<Integer, Integer, Integer, List<DatapackEntry>> entries;

    public RegularFormat(TriFunction<Integer, Integer, Integer, List<DatapackEntry>> entries) {
        this.entries = entries;
    }

    @Override
    public TriFunction<Integer, Integer, Integer, List<DatapackEntry>> entries() {
        return entries;
    }
}
