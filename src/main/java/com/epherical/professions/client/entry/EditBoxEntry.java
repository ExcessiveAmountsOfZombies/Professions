package com.epherical.professions.client.entry;

import com.epherical.professions.client.screen.CommonDataScreen;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public abstract class EditBoxEntry<T, V extends EditBoxEntry<?, ?>> extends DatapackEntry<T, V> {

    protected final String usage;
    protected EditBox box;

    public EditBoxEntry(int x, int y, int width, String usage, String defaultValue) {
        this(x, y, width, usage, defaultValue, Optional.of(usage));
    }

    public EditBoxEntry(int x, int y, int width, String usage, String defaultValue, Type... types) {
        this(x, y, width, usage, defaultValue, Optional.of(usage), types);
    }

    public EditBoxEntry(int i, int j, int k, String usage, String defaultValue, Optional<String> key, Type... types) {
        super(i, j, k, key, types);
        this.usage = usage;
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        this.box = new EditBox(font, this.x + width / 2 - 50, j + 8, 250, 22, Component.nullToEmpty(""));
        this.box.setVisible(true);
        this.box.setMaxLength(256);
        this.box.setBordered(false);
        this.box.setValue(defaultValue);
        this.box.setTextColor(TEXT_COLOR);
        children.add(box);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        font.drawShadow(poseStack, usage, x + 3 + getXScroll(), y + 8 + getYScroll(), 0xFFFFFF);
        this.box.x = (this.x + this.width - (font.width(box.getValue()) / 2)) / 2;
        this.box.y = y + 8 + getYScroll();
    }

    @Override
    public JsonElement getSerializedValue() {
        if (box.getValue().length() > 0) {
            return new JsonPrimitive(box.getValue());
        } else {
            return JsonNull.INSTANCE;
        }
    }

    @Override
    public void tick(CommonDataScreen screen) {
        super.tick(screen);
        box.tick();
        box.setMessage(Component.nullToEmpty(box.getValue()));
    }

    @Override
    public void onRebuild(CommonDataScreen screen) {
        rebuildTinyButtons(screen);
        screen.addChild(box);
        screen.addChild(this);
    }

    public void setValue(String value) {
        box.setValue(value);
    }

    @Override
    public String getType() {
        return "String";
    }

    @Override
    public String toString() {
        return "EditBoxEntry{" +
                "usage='" + usage + '\'' +
                ", box=" + box +
                "} " + super.toString();
    }
}
