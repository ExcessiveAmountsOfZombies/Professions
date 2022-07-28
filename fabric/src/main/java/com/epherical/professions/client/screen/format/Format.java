package com.epherical.professions.client.screen.format;

import com.epherical.professions.client.screen.entry.DatapackEntry;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;

public interface Format {

    TriFunction<Integer, Integer, Integer, List<DatapackEntry>> entries();
}
