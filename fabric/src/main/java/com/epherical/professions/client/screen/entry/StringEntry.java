package com.epherical.professions.client.screen.entry;

import com.epherical.professions.client.screen.DatapackScreen;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.List;
import java.util.Optional;

public class StringEntry extends DatapackEntry {

    private final String usage;
    private EditBox box;

    public StringEntry(int x, int y, int width, String usage, String defaultValue) {
        this(x, y, width, usage, defaultValue, Optional.empty());
    }

    public StringEntry(int i, int j, int k, String usage, String defaultValue, Optional<String> key) {
        super(i, j, k, key);
        this.usage = usage;
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        this.box = new EditBox(font, this.x + width / 2 - 50, j + 8, 250, 22, Component.nullToEmpty(""));
        this.box.setVisible(true);
        this.box.setMaxLength(100);
        this.box.setBordered(false);
        this.box.setValue(defaultValue);
        this.box.setTextColor(0x0095ba);
        children.add(box);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        font.drawShadow(poseStack, usage, x + 3 + getXScroll(), y + 8 + getYScroll(), 0xFFFFFF);
        //drawCenteredString(poseStack, font, this.box.getValue(), this.width / 2, y + 8, 0x0095ba);
        if (isHoveredOrFocused()) {
            //renderToolTip(poseStack, mouseX, mouseY, this.box.getValue());
        }
        this.box.y = y + 8 + getYScroll();
        //this.box.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    public JsonElement getSerializedValue() {
        return new JsonPrimitive(box.getValue());
    }

    @Override
    public void tick(DatapackScreen screen) {
        super.tick(screen);
        box.tick();
        box.setMessage(Component.nullToEmpty(box.getValue()));
    }

    @Override
    public String getType() {
        return "String";
    }
}
