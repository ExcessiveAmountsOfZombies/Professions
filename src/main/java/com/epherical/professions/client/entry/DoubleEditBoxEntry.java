package com.epherical.professions.client.entry;

import com.epherical.professions.client.screen.CommonDataScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public class DoubleEditBoxEntry<OBJ> extends EditBoxEntry<OBJ, DoubleEditBoxEntry<OBJ>> {

    protected EditBox keyBox;
    private final Deserializer<OBJ, DoubleEditBoxEntry<OBJ>> deserializer;

    public DoubleEditBoxEntry(int x, int y, int width, String usage, String defaultValue, Deserializer<OBJ, DoubleEditBoxEntry<OBJ>> deserializer) {
        this(x, y, width, usage, defaultValue, Optional.of(usage), deserializer);
    }

    public DoubleEditBoxEntry(int x, int y, int width, String usage, String defaultValue, Deserializer<OBJ, DoubleEditBoxEntry<OBJ>> deserializer, Type... types) {
        this(x, y, width, usage, defaultValue, Optional.of(usage), deserializer, types);
    }

    public DoubleEditBoxEntry(int i, int j, int k, String usage, String defaultValue, Optional<String> key, Deserializer<OBJ, DoubleEditBoxEntry<OBJ>> deserializer, Type... types) {
        super(i, j, k, usage, defaultValue, key, types);
        this.deserializer = deserializer;
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        this.keyBox = new EditBox(font, this.x + 30, j + 8, 250, 22, Component.nullToEmpty(""));
        this.keyBox.setVisible(true);
        this.keyBox.setMaxLength(256);
        this.keyBox.setBordered(false);
        this.keyBox.setValue(defaultValue);
        this.keyBox.setTextColor(TEXT_COLOR);
        this.children.add(keyBox);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
        this.keyBox.x = (this.x + 30);
        this.keyBox.y = y + 8 + getYScroll();
    }

    public void setKeyValue(String key) {
        this.keyBox.setValue(key);
    }

    @Override
    public void onRebuild(CommonDataScreen screen) {
        rebuildTinyButtons(screen);
        screen.addChild(box);
        screen.addChild(keyBox);
        screen.addChild(this);
    }

    @Override
    public void deserialize(OBJ object) {
        deserializer.deserialize(object, this);
    }

    @Override
    public Optional<String> getSerializationKey() {
        return Optional.of(keyBox.getValue());
    }

    @Override
    public String getType() {
        return "Key/Value";
    }
}
