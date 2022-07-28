package com.epherical.professions.client.screen.entry;

import com.epherical.professions.client.screen.DatapackScreen;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class MultipleTypeEntry extends DatapackEntry {

    private int currentSelection;
    private final DatapackEntry[] types;

    protected boolean needsRefresh;

    public MultipleTypeEntry(int i, int j, int k, DatapackEntry... entries) {
        super(i, j, k, 23);
        currentSelection = 0;
        this.types = entries;
        children.add(entries[currentSelection]);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
        types[currentSelection].render(poseStack, mouseX, mouseY, partialTick);
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        font.drawShadow(poseStack, types[currentSelection].getType(), x + 3 + getXScroll(), y + 8 + getYScroll(), 0xFFFFFF);
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.renderButton(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        currentSelection++;
        if (currentSelection >= types.length) {
            currentSelection = 0;
        }
        needsRefresh = true;
    }

    @Override
    public void tick(DatapackScreen screen) {
        super.tick(screen);
        this.types[currentSelection].tick(screen);
        if (needsRefresh) {
            screen.markScreenDirty();
            children.remove(0);
            DatapackEntry object = types[currentSelection];
            children.add(object);
            needsRefresh = false;
        }
    }

    @Override
    public void onRebuild(DatapackScreen screen) {
        // any direct children go before this entry.
        rebuildTinyButtons(screen);
        screen.addChild(this);
        DatapackEntry object = types[currentSelection];
        object.onRebuild(screen);
    }

    @Override
    public String getType() {
        return "Multiple Choices";
    }

    public JsonElement getSerializedValue() {
        return types[currentSelection].getSerializedValue();
    }
}
