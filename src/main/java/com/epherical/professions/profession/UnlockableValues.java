package com.epherical.professions.profession;

import com.epherical.professions.profession.unlock.Unlock;

import java.util.ArrayList;
import java.util.List;

public class UnlockableValues<T extends Unlock.Singular<?>> {

    private List<T> values;

    public UnlockableValues(T seedValue) {
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
