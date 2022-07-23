package com.epherical.professions.client.screen.entry;

import java.util.List;

public class CompoundEntry extends DatapackEntry {

    public CompoundEntry(int i, int j, int k, List<DatapackEntry> entries) {
        super(i, j, k, 0);
        children.addAll(entries);

        // also need to start working on adding a scroll bar
    }
}
