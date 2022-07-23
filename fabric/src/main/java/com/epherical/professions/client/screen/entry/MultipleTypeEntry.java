package com.epherical.professions.client.screen.entry;

import com.mojang.blaze3d.vertex.PoseStack;

public class MultipleTypeEntry extends DatapackEntry {

    private int currentSelection;
    private final DatapackEntry[] types;

    public MultipleTypeEntry(int i, int j, int k, DatapackEntry... entries) {
        super(i, j, k, 0);
        currentSelection = 0;
        this.types = entries;
        children.add(entries[0]);
        // todo; on click we will remove this child and put the other one in its place
    }


    @Override
    public void initPosition(int initialX, int initialY) {
        super.initPosition(initialX, initialY);
        int start = width - 25;
        this.types[currentSelection].x = x + start;
        this.types[currentSelection].y = y;
        System.out.println("init pos");
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
        types[currentSelection].render(poseStack, mouseX, mouseY, partialTick);
    }
}
