package com.epherical.professions.util;

import java.util.ArrayList;
import java.util.List;

public class SeededValueList<T> {

    private List<T> values;

    public SeededValueList(T seedValue) {
        this.values = new ArrayList<>();
        this.values.add(seedValue);
    }

    public List<T> getValues() {
        return values;
    }

    public void addValue(T value) {
        this.values.add(value);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }
}
