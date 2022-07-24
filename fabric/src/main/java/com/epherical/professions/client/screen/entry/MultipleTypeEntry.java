package com.epherical.professions.client.screen.entry;

import com.epherical.professions.client.screen.DatapackScreen;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;

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
        if (needsRefresh) {
            AbstractWidget remove = children.remove(0);
            if (remove instanceof DatapackEntry entry) {
                for (AbstractWidget child : entry.children) {
                    for (AbstractWidget widget1 : flattenEntries(Lists.newArrayList(child), child)) {
                        screen.removeWidget(widget1);
                    }
                }
                screen.removeWidget(entry);
            }
            int index = screen.children.indexOf(this);
            DatapackEntry object = types[currentSelection];
            object.setX(x + 7);
            object.setY(y + object.getHeight());
            for (AbstractWidget child : object.children) {
                for (AbstractWidget widget : flattenEntries(Lists.newArrayList(child), child)) {
                    // todo; hmm
                    screen.children.add(index + 1, widget);
                    screen.renderables.add(index + 1, widget);
                    //screen.addRenderableWidget(child);
                }
                /*screen.children.add(index + 1, child);
                screen.addRenderableOnly(child);*/
            }
            screen.children.add(index + 1, object);
            screen.renderables.add(index + 1, object);
            children.add(object);
            screen.adjustEntries = true;
            needsRefresh = false;
        }
    }

    @Override
    public String getType() {
        return "Multiple Choices";
    }
}