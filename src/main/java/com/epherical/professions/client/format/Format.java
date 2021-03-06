package com.epherical.professions.client.format;

import com.epherical.professions.client.entry.DatapackEntry;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;

public interface Format {

    TriFunction<Integer, Integer, Integer, List<DatapackEntry>> entries();
}
