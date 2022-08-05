package com.epherical.professions.client.entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

import java.util.Optional;

public class StringEntry<OBJ> extends EditBoxEntry<OBJ, StringEntry<OBJ>> {

    public StringEntry(int x, int y, int width, String usage, String defaultValue) {
        this(x, y, width, usage, defaultValue, Optional.of(usage));
    }

    public StringEntry(int x, int y, int width, String usage, String defaultValue, Type... types) {
        this(x, y, width, usage, defaultValue, Optional.of(usage), types);
    }

    public StringEntry(int i, int j, int k, String usage, String defaultValue, Optional<String> key, Type... types) {
        super(i, j, k, usage, defaultValue, key, types);
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
    }

    @Override
    public String getType() {
        return "String";
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
