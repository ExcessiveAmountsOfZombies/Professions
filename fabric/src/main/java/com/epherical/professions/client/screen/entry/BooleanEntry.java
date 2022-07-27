package com.epherical.professions.client.screen.entry;

import com.epherical.professions.client.screen.DatapackScreen;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

import java.util.Optional;

public class BooleanEntry extends DatapackEntry {

    private String usage;
    private boolean value;

    public BooleanEntry(int x, int y, int width, String usage, boolean defaultValue) {
        this(x, y, width, usage, defaultValue, Optional.empty());
    }

    public BooleanEntry(int x, int y, int width, String usage, boolean defaultValue, Optional<String> serializationKey) {
        super(x, y, width, serializationKey);
        this.usage = usage;
        this.value = defaultValue;
    }

    @Override
    public void onRebuild(DatapackScreen screen) {
        screen.addChild(this);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        font.drawShadow(poseStack, usage, x + 3 + getXScroll(), y + 8 + getYScroll(), 0xFFFFFF);
        drawCenteredString(poseStack, font, String.valueOf(value), (this.width / 2) + getXScroll(), (y + 8) + getYScroll(), TEXT_COLOR);
        //this.box.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        this.value = !value;
    }

    @Override
    public String getType() {
        return "Boolean";
    }

    public JsonElement getSerializedValue() {
        return new JsonPrimitive(value);
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "BooleanEntry{" +
                "usage='" + usage + '\'' +
                ", value=" + value +
                "} " + super.toString();
    }
}
