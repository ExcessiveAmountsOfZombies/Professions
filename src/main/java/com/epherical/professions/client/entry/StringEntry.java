package com.epherical.professions.client.entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

import java.util.Optional;

public class StringEntry<OBJ> extends EditBoxEntry<OBJ, StringEntry<OBJ>> {

    private final Deserializer<OBJ, StringEntry<OBJ>> deserializer;

    public StringEntry(int x, int y, int width, String usage, String defaultValue, Deserializer<OBJ, StringEntry<OBJ>> deserializer) {
        this(x, y, width, usage, defaultValue, Optional.of(usage), deserializer);
    }

    public StringEntry(int x, int y, int width, String usage, String defaultValue, Deserializer<OBJ, StringEntry<OBJ>> deserializer, Type... types) {
        this(x, y, width, usage, defaultValue, Optional.of(usage), deserializer, types);
    }

    public StringEntry(int i, int j, int k, String usage, String defaultValue, Optional<String> key, Deserializer<OBJ, StringEntry<OBJ>> deserializer, Type... types) {
        super(i, j, k, usage, defaultValue, key, types);
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        this.deserializer = deserializer;
    }

    public StringEntry<OBJ> setEditMaxLength(int maxLength) {
        box.setMaxLength(maxLength);
        return this;
    }


    @Override
    public void deserialize(OBJ object) {
        deserializer.deserialize(object, this);
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
